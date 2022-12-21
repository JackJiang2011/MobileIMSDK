#import "NavigationController.h"

// 16进制颜色值，如：#000000 , 注意：在使用的时候hexValue写成：0x000000
#define HexColor(hexValue) [UIColor colorWithRed:((float)(((hexValue) & 0xFF0000) >> 16))/255.0 green:((float)(((hexValue) & 0xFF00) >> 8))/255.0 blue:((float)((hexValue) & 0xFF))/255.0 alpha:1.0]

@implementation NavigationController

- (void)viewDidLoad
{
	[super viewDidLoad];

	self.navigationBar.translucent = NO;
    
    // 标题栏背景色
    self.navigationBar.barTintColor = HexColor(0xfafafa);
    
    // 设置默认左右按钮的颜色
    self.navigationBar.tintColor = HexColor(0xc6391e);
//	self.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:UI_DEFAULT_HILIGHT_COLOR};
    
    // 标题栏字体大小和标题颜色
    [self.navigationBar setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:19]//20]
                                                 ,NSForegroundColorAttributeName:HexColor(0x2c2f36)}];
    
    // 设置 navigationBar 下面的横线（记住要用@2x、@3x命名，否则按@1x进行填充时显示的线条不只一个像素高度，就很难看。另，
    // 也不建议用春色UIImage对象，因为代码中设置的像素高度并不是绝对像素，所以会存在横线显示高度粗线难控制的问题）
    [self.navigationBar setShadowImage:[UIImage imageNamed:@"navigation_bar_shadow_image"]];
        
    // 适配iOS15，如不适配则每个界面标题栏都会变黑色，很难看，参考资料：https://www.jianshu.com/p/6dcf60b645ec
    if (@available(iOS 13.0, *)) {
        UINavigationBarAppearance *barApp = [UINavigationBarAppearance new];
        
        // 解决标题栏变透明全黑的问题
        barApp.backgroundColor = HexColor(0xfafafa);
        barApp.backgroundEffect = nil;// 去掉半透明效果
        
        // 如果不设置此项，则 self.navigationBar.shadowImage 的设置是没效果的（仍然会显示系统默认的那条深灰色线）
        barApp.shadowImage = [UIImage imageNamed:@"navigation_bar_shadow_image"];
        
        self.navigationBar.scrollEdgeAppearance = barApp;
        self.navigationBar.standardAppearance = barApp;
    }
}

- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleDefault;//UIStatusBarStyleLightContent;
}

//- (void)viewDidLoad {
//    [super viewDidLoad];
//    [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
//
//    UINavigationBar *bar = [UINavigationBar appearance];
//    CGFloat rgb = 0.1;
//    bar.barTintColor = [UIColor colorWithRed:rgb green:rgb blue:rgb alpha:0.9];
//    bar.tintColor = [UIColor whiteColor];
//    bar.titleTextAttributes = @{NSForegroundColorAttributeName : [UIColor whiteColor]};
//}

-(void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    if (self.viewControllers.count)
    {
        viewController.hidesBottomBarWhenPushed = YES;
    }
    [super pushViewController:viewController animated:animated];
}

@end

