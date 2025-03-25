import { Component, Input, ElementRef, AfterContentInit } from '@angular/core';

@Component({
  selector: 'aem-component',
  styleUrls: [],
  template: ``
})
export class AEMComponent {
  @Input() componentName: string;
    
  public showPlaceholder: boolean = false;
  
  constructor(element: ElementRef) {
  }
  
  public setShowPlaceholder(isEmpty: boolean): void {
    this.showPlaceholder = (this.getWcmMode() == 'edit' || this.getWcmMode() == 'preview') && isEmpty;
  }
  
  public getWcmMode():string {
          return this.getMetaPropertyValue('cq:wcmmode');
  }

  
  public  isBrowser():boolean {
          try {
              return typeof window !== 'undefined';
          } catch (e) {
              return false;
          }
      }
      
  public getMetaPropertyValue(propertyName:string):any {
          if (this.isBrowser()) {
              const meta = document.head.querySelector('meta[property="' + propertyName + '"]');
              // @ts-ignore
              return meta && meta.content;
          }
      }
}
