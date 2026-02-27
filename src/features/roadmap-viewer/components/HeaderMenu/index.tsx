'use client';

import { BarChart3, Moon, Download, Camera, Settings } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
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
      <DropdownMenuContent align="start">
        <DropdownMenuItem>
          <BarChart3 className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_STATISTICS}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Moon className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_DARK_MODE}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Download className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_EXPORT}
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Camera className="mr-2 h-4 w-4" />
          {VIEWER_MESSAGES.MENU_SAVE_IMAGE}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
