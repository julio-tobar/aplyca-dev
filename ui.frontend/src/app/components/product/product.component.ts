import { Component, ElementRef, Input, AfterContentInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

import { AEMComponent } from '../aem-component/aem-component.component';

@Component({
  selector: 'app-product',
  styleUrls: ['./product.component.css'],
  templateUrl: './product.component.html'
})
export class ProductComponent extends AEMComponent implements AfterContentInit {
  @Input() componentName: string = undefined;
  @Input() id: string;
  @Input() productItem: any;
  
  constructor(private sanitizer: DomSanitizer, 
                      element: ElementRef) {
    super(element);
  }
  
  ngAfterContentInit(): void {
     super.setShowPlaceholder(this.productItem == undefined);
  }
}
