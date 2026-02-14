import time

import jwt
from django.test import TestCase, override_settings
from rest_framework.test import APIClient


@override_settings(
    AI_AUTH_ENABLED=True,
    AI_AUTH_JWT_SECRET="test-secret",
    AI_AUTH_JWT_ALGORITHM="HS256",
    AI_AUTH_JWT_LEEWAY_SECONDS=0,
)
class AIApiAuthTests(TestCase):
    def setUp(self) -> None:
        self.client = APIClient()

    def _issue_token(self, *, sub: str, roadmap_id: str | None, permissions: list[str]):
        now = int(time.time())
        payload: dict[str, object] = {
            "sub": sub,
            "permissions": permissions,
            "iat": now,
            "exp": now + 60 * 60,
        }
        if roadmap_id is not None:
            payload["roadmapId"] = roadmap_id
        token = jwt.encode(payload, "test-secret", algorithm="HS256")
        if isinstance(token, bytes):
            token = token.decode("utf-8")
        return token

    def test_requires_token(self):
        resp = self.client.get("/ai/related-roadmaps")
        self.assertEqual(resp.status_code, 401)

    def test_allows_with_valid_token(self):
        token = self._issue_token(sub="user-1", roadmap_id="rm_frontend", permissions=["EDIT"])
        resp = self.client.get(
            "/ai/related-roadmaps",
            HTTP_AUTHORIZATION=f"Bearer {token}",
        )
        self.assertEqual(resp.status_code, 200)
        self.assertIn("roadmap_id", resp.json())

    def test_read_permission_allows_get_but_denies_post(self):
        token = self._issue_token(sub="user-1", roadmap_id="rm_frontend", permissions=["READ"])

        get_resp = self.client.get(
            "/ai/related-roadmaps",
            HTTP_AUTHORIZATION=f"Bearer {token}",
        )
        self.assertEqual(get_resp.status_code, 200)

        post_resp = self.client.post(
            "/ai/init-data",
            data={"content": "hello", "data_type": "text"},
            format="json",
            HTTP_AUTHORIZATION=f"Bearer {token}",
        )
        self.assertEqual(post_resp.status_code, 403)

    def test_edit_permission_allows_post_with_token_roadmap(self):
        token = self._issue_token(sub="user-1", roadmap_id="rm_frontend", permissions=["EDIT"])

        resp = self.client.post(
            "/ai/init-data",
            data={"content": "hello", "data_type": "text"},
            format="json",
            HTTP_AUTHORIZATION=f"Bearer {token}",
        )
        self.assertEqual(resp.status_code, 201)
        self.assertEqual(resp.json()["roadmap_id"], "rm_frontend")

    def test_scope_mismatch_rejected(self):
        token = self._issue_token(sub="user-1", roadmap_id=None, permissions=["READ"])

        resp = self.client.get(
            "/ai/learning-pattern?user_id=user-2",
            HTTP_AUTHORIZATION=f"Bearer {token}",
        )
        self.assertEqual(resp.status_code, 403)
