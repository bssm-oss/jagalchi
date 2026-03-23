'use client';

import { Settings } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { VIEWER_MESSAGES } from '@/constants/messages';

export function HeaderMenu() {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm">
          <Settings className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="start" className="min-w-[180px]">
        <DropdownMenuItem>{VIEWER_MESSAGES.MENU_STATISTICS}</DropdownMenuItem>
        <DropdownMenuSub>
          <DropdownMenuSubTrigger>{VIEWER_MESSAGES.MENU_EXPORT}</DropdownMenuSubTrigger>
          <DropdownMenuSubContent>
            <DropdownMenuItem>{VIEWER_MESSAGES.IMAGE_PNG}</DropdownMenuItem>
            <DropdownMenuItem>{VIEWER_MESSAGES.IMAGE_JPG}</DropdownMenuItem>
            <DropdownMenuItem>{VIEWER_MESSAGES.IMAGE_SVG}</DropdownMenuItem>
            <DropdownMenuItem>{VIEWER_MESSAGES.EXPORT_PDF}</DropdownMenuItem>
            <DropdownMenuItem>{VIEWER_MESSAGES.EXPORT_MARKDOWN}</DropdownMenuItem>
            <DropdownMenuItem>{VIEWER_MESSAGES.EXPORT_JSON}</DropdownMenuItem>
          </DropdownMenuSubContent>
        </DropdownMenuSub>
        <DropdownMenuItem>{VIEWER_MESSAGES.MENU_IMPORT_JSON}</DropdownMenuItem>
        <DropdownMenuItem>{VIEWER_MESSAGES.MENU_DARK_MODE}</DropdownMenuItem>
        <DropdownMenuItem>{VIEWER_MESSAGES.MENU_VERSION}</DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
