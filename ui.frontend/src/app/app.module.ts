import { SpaAngularEditableComponentsModule } from '@adobe/aem-angular-editable-components';
import { APP_BASE_HREF } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {MapTo} from '@adobe/aem-angular-editable-components';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PageComponent } from './components/page/page.component';
import { ModelManagerService } from './components/model-manager.service';
import { SafeHtmlPipe } from './pipes/SafeHtmlPipe';

import './components/container/container.component';
import './components/responsive-grid/responsive-grid.component';
import { TextComponent } from './components/text/text.component';
import { BannersComponent } from './components/banners/banners.component';
import { ProductComponent } from './components/product/product.component';
import { ProductgridComponent } from './components/productgrid/productgrid.component';

MapTo('aplyca-julio/components/text')(TextComponent);
MapTo('aplyca-julio/components/banners')(BannersComponent);
MapTo('aplyca-julio/components/product')(ProductComponent);
MapTo('aplyca-julio/components/productgrid')(ProductgridComponent);

@NgModule({
  imports: [
    BrowserModule,
    SpaAngularEditableComponentsModule,
    AppRoutingModule
  ],
  providers: [ ModelManagerService,
    { provide: APP_BASE_HREF, useValue: '/' } ],
  declarations: [SafeHtmlPipe, AppComponent, PageComponent, TextComponent,  BannersComponent, ProductComponent, ProductgridComponent],
  entryComponents: [PageComponent],
  bootstrap: [ AppComponent ]
})
export class AppModule {}
