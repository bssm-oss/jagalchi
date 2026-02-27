'use client';

import { Download, FileText, FileJson } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { VIEWER_MESSAGES } from '@/constants/messages';

export function HeaderExportMenu() {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm">
          <Download className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_EXPORT}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start">
        <DropdownMenuItem>
          <FileText className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.EXPORT_MARKDOWN}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <FileText className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.EXPORT_PDF}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <FileJson className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.EXPORT_JSON}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
