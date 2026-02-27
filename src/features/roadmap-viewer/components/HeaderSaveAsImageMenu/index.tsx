'use client';

import { Camera, Image } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { VIEWER_MESSAGES } from '@/constants/messages';

export function HeaderSaveAsImageMenu() {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm">
          <Camera className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_SAVE_IMAGE}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start">
        <DropdownMenuItem>
          <Image className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.IMAGE_PNG}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Image className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.IMAGE_JPG}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Image className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.IMAGE_SVG}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
