import { Component, ElementRef, Input, AfterContentInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

import { AEMComponent } from '../aem-component/aem-component.component';

@Component({
  selector: 'app-banners',
  templateUrl: './banners.component.html',
  styleUrls: ['./banners.component.css']
})
export class BannersComponent extends AEMComponent implements AfterContentInit {
  @Input() banners: any;
  @Input() id: string;
  @Input() contentFragmentPath: any;
  @Input() quantity: string;
  
  constructor(private sanitizer: DomSanitizer, 
                        element: ElementRef) {
      super(element);
    }
    
    ngAfterContentInit(): void {
       super.setShowPlaceholder(this.banners == undefined || this.banners.length == 0);  
    }
}
