import { Component, ElementRef, Input, AfterContentInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

import { AEMComponent } from '../aem-component/aem-component.component';

@Component({
  selector: 'app-text',
  styleUrls: ['./text.component.css'],
  templateUrl: './text.component.html'
})
export class TextComponent extends AEMComponent implements AfterContentInit {
  @Input() text: string = undefined;
  @Input() company: string;
  @Input() id: string;
  
  constructor(private sanitizer: DomSanitizer, 
                      element: ElementRef) {
    super(element);
  }
  
  ngAfterContentInit(): void {
     super.setShowPlaceholder(this.text == undefined);
  }
}
