/*
 * Copyright (C) 2024  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK-鸿蒙Next客户端 Project. All rights reserved.
 *
 * 联系邮件：jack.jiang@52im.net
 * 联系微信：hellojackjiang
 * 联系QQ： 413980957
 * 技术社区：http://www.52im.net
 */

@Preview
@Component
export struct CustomeTitleBar {

  // 返回按钮图片
  backImg: Resource = $r("app.media.widget_title_btn_back_normal");

  build() {

    Column() {

      Row() {

        Row() {
          // 返回按钮
          Button() {
            Image(this.backImg)
              .objectFit(ImageFit.Contain)
              .width('100%')
              .height('100%')
          }
          .width(60)
          .height(50)
          .backgroundColor("#00000000")
          .onClick(() => {
            // this.backFunc ? this.backFunc : router.back();
          });
        }

        Text('这是标题栏这是标题栏这是标题栏这是标题栏这是标题栏这是标题栏这是标题栏')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)
          .fontSize(20)
          .maxLines(1)
          .fontColor('#2c2f36')
          .fontWeight(500)
          .ellipsisMode(EllipsisMode.CENTER)
          .textOverflow({overflow: TextOverflow.Ellipsis})

        Row() {
          Button() {
            Image($r('app.media.widget_title_btn_mpre_normal'))
              .objectFit(ImageFit.Contain)
              .width('100%')
              .height('100%')
          }
          .width(50)
          .height(50)
          .backgroundColor("#00000000")
          .onClick(() => {
            // this.backFunc ? this.backFunc : router.back();
          });
          Button() {
            Image($r('app.media.widget_title_btn_mpre_normal'))
              .objectFit(ImageFit.Contain)
              .width('100%')
              .height('100%')
          }
          .width(50)
          .height(50)
          .backgroundColor("#00000000")
          .onClick(() => {
            // this.backFunc ? this.backFunc : router.back();
          });
        }
      }

      this.BottomLine()
    }
    .width('100%')
    .height(50)
    .backgroundColor('#fafafa')
  }

  @Builder
  BottomLine() {
    Line()
      .height('1px')
      .width('100%')
      .backgroundColor('#dbdbdb')
  }
}
