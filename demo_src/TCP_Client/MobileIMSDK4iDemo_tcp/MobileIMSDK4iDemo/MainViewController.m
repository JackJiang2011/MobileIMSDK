//  ----------------------------------------------------------------------
//  Copyright (C) 2021  即时通讯网(52im.net) & Jack Jiang.
//  The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
//  All rights reserved.
//
//  > Github地址: https://github.com/JackJiang2011/MobileIMSDK
//  > 文档地址:    http://www.52im.net/forum-89-1.html
//  > 技术社区：   http://www.52im.net/
//  > 技术交流群： 215477170 (http://www.52im.net/topic-qqgroup.html)
//  > 作者公众号： “即时通讯技术圈】”，欢迎关注！
//  > 联系作者：   http://www.52im.net/thread-2792-1-1.html
//
//  "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
//  ----------------------------------------------------------------------

#import "MainViewController.h"
#import "ConfigEntity.h"
#import "ToolKits.h"
#import "ClientCoreSDK.h"
#import "LocalDataSender.h"
#import "ErrorCode.h"
#import "Protocal.h"
#import "AutoReLoginDaemon.h"
#import "KeepAliveDaemon.h"
#import "QoS4ReciveDaemon.h"
#import "QoS4SendDaemon.h"
#import "ChatBaseEventImpl.h"
#import "ChatInfoTableViewCellDTO.h"
#import "ChatInfoTableViewCell.h"
#import "UIViewController+Ext.h"
#import "IMClientManager.h"
#import "AppDelegate.h"
#import "ProtocalType.h"


////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 静态常量
////////////////////////////////////////////////////////////////////////////////////////////

static const int TABLE_CELL_COLOR_BLACK      = 0;
static const int TABLE_CELL_COLOR_BLUE       = 1;
static const int TABLE_CELL_COLOR_BRIGHT_RED = 2;
static const int TABLE_CELL_COLOR_RED        = 3;
static const int TABLE_CELL_COLOR_GREEN      = 4;


////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 私有API
////////////////////////////////////////////////////////////////////////////////////////////

@interface MainViewController ()
{
    // cht info time
    NSDateFormatter *hhmmssFormat;
}

// 用于主界面表格的数据显示
@property (nonatomic, retain) NSMutableArray* chatInfoList;

@end


/////////////////////////////////////////////////////////////////////////////////////////////
#pragma mark - 本类的代码实现
/////////////////////////////////////////////////////////////////////////////////////////////

@implementation MainViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]))
    {
        //
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // 设置顶部导航标题栏为不透明，否则在ios7以上系统会挡住下方的页面内容
    self.navigationController.navigationBar.translucent = NO;
    self.title = @"MobileIMSDK_TCP Demo";
    
    // chat info time
    hhmmssFormat = [[NSDateFormatter alloc] init];
    [hhmmssFormat setDateFormat:@"HH:mm:ss"];
    
    // 表格基本设置
    self.chatInfoList = [[NSMutableArray alloc] init];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    
    // just for debug START
    [self initObserversForDEBUG];
    // just for debug END
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // Refresh MobileIMSDK userId to show
    [self refreshConnecteStatus];
    
    // just for debug START
    // Refresh MobileIMSDK background status to show
    [self refreshMobileIKSDKThreadStatusForDEBUG];
    // just for debug END
    
    // 将当前账号显示出来
    self.myUserId.text = [ClientCoreSDK sharedInstance].currentLoginUserId;
}

- (IBAction)signOut:(id)sender
{
    // 退出登陆
    [self doLogout];
    // 退出程序
    [self doExit];
}

- (IBAction)send:(id)sender
{
    [self doSendMessage];
}

- (void) refreshConnecteStatus
{
    BOOL connectedToServer = [ClientCoreSDK sharedInstance].connectedToServer;
    if(connectedToServer) {
        self.connectStatus.text = @"通信正常";
        self.connectStatus.textColor = [UIColor colorWithRed:91/255.0f green:198/255.0f blue:72/255.0f alpha:1];
        [self.connectStatusIcon setImage:[UIImage imageNamed:@"green"]];
    }
    else{
        self.connectStatus.text = @"连接断开";
        self.connectStatus.textColor = [UIColor colorWithRed:255/255.0f green:0/255.0f blue:255/255.0f alpha:1];
        [self.connectStatusIcon setImage:[UIImage imageNamed:@"red"]];
    }
}

- (void)doSendMessage
{
    NSString *dicStr = self.messageField.text;
    if ([dicStr length] == 0)
    {
        [self E_showToastInfo:@"提示" withContent:@"请输入消息内容！" onParent:self.view];
        return;
    }

    NSString *friendIdStr = self.friendId.text;
    if ([friendIdStr length] == 0)
    {
        [self E_showToastInfo:@"提示" withContent:@"请输入对方id！" onParent:self.view];
        return;
    }

    //
    [self showIMInfo_black:[NSString stringWithFormat:@"我对%@说：%@", friendIdStr, dicStr]];

    // 发送消息
    int code = [[LocalDataSender sharedInstance] sendCommonDataWithStr:dicStr toUserId:friendIdStr qos:YES fp:nil withTypeu:-1];
    if(code == COMMON_CODE_OK)
    {
//      [self showToastInfo:@"提示" withContent:@"您的消息已成功发出。。。"];
    }
    else
    {
        NSString *msg = [NSString stringWithFormat:@"您的消息发送失败，错误码：%d", code];
        [self E_showToastInfo:@"错误" withContent:msg onParent:self.view];
    }
}

- (void)doLogout
{
    // 发出退出登陆请求包
    int code = [[LocalDataSender sharedInstance] sendLoginout];
    if(code == COMMON_CODE_OK)
    {
        [self E_showToastInfo:@"提示" withContent:@"注销登陆请求已完成。。。" onParent:self.view];
        [self refreshConnecteStatus];
    }
    else
    {
        NSString *msg = [NSString stringWithFormat:@"注销登陆请求发送失败，错误码：%d", code];
        [self E_showToastInfo:@"错误" withContent:msg onParent:self.view];
    }

    //## BUG FIX: 20170713 START by JackJiang
    // 退出登陆时记得一定要调用此行，不然不退出APP的情况下再登陆时会报 code=203错误哦！
    [[IMClientManager sharedInstance] resetInitFlag];
    //## BUG FIX: 20170713 END by JackJiang
}

- (void)doExit
{
    UIWindow *window = CurAppDelegate.window;
    [UIView animateWithDuration:1.0f animations:^{
        window.alpha = 0;
        window.frame = CGRectMake(0, window.bounds.size.width, 0, 0);
    } completion:^(BOOL finished) {
        exit(0);
    }];
    //exit(0);
}

//===============================================================================  just for 信息显示 START
#pragma mark - 以下代码用于在Demo下方的信息内容表格里显示即时通讯相关信息

- (void) showIMInfo_black:(NSString*)txt
{
    [self showIMInfo:txt withColorType:TABLE_CELL_COLOR_BLACK];
}

- (void) showIMInfo_blue:(NSString*)txt
{
    [self showIMInfo:txt withColorType:TABLE_CELL_COLOR_BLUE];
}

- (void) showIMInfo_brightred:(NSString*)txt
{
    [self showIMInfo:txt withColorType:TABLE_CELL_COLOR_BRIGHT_RED];
}

- (void) showIMInfo_red:(NSString*)txt
{
    [self showIMInfo:txt withColorType:TABLE_CELL_COLOR_RED];
}

- (void) showIMInfo_green:(NSString*)txt
{
    [self showIMInfo:txt withColorType:TABLE_CELL_COLOR_GREEN];
}

- (void) showIMInfo:(NSString*)txt withColorType:(int)colorType
{
    ChatInfoTableViewCellDTO *dto = [[ChatInfoTableViewCellDTO alloc] init];
    dto.colorType = colorType;
    dto.content = [NSString stringWithFormat:@"[%@] %@", [hhmmssFormat stringFromDate:[[NSDate alloc] init]], txt];
    [self.chatInfoList addObject:dto];
    [self.tableView reloadData];
    
    // 自动显示最后一行
    NSInteger s = [self.tableView numberOfSections];
    if (s<1) return;
    NSInteger r = [self.tableView numberOfRowsInSection:s-1];
    if (r<1) return;
    NSIndexPath *ip = [NSIndexPath indexPathForRow:r-1 inSection:s-1];
    [self.tableView scrollToRowAtIndexPath:ip atScrollPosition:UITableViewScrollPositionBottom animated:YES];
}
//===============================================================================  just for 信息显示 END

//===============================================================================  just for debug START
#pragma mark - 以下代码用于DEBUG时显示各种状态

- (void) refreshMobileIKSDKThreadStatusForDEBUG
{
    [self showDebugStatusImage:([[AutoReLoginDaemon sharedInstance] isAutoReLoginRunning]?1:0) forImageView:self.iviewAutoRelogin];
    [self showDebugStatusImage:([[KeepAliveDaemon sharedInstance] isKeepAliveRunning]?1:0) forImageView:self.iviewKeepAlive];
    [self showDebugStatusImage:([[QoS4SendDaemon sharedInstance] isRunning]?1:0) forImageView:self.iviewQoSSend];
    [self showDebugStatusImage:([[QoS4ReciveDaemon sharedInstance] isRunning]?1:0) forImageView:self.iviewQoSReceive];
}

- (void) initObserversForDEBUG
{
    [self setupAnimationForStatusImage:self.iviewAutoRelogin];
    [self setupAnimationForStatusImage:self.iviewKeepAlive];
    [self setupAnimationForStatusImage:self.iviewQoSSend];
    [self setupAnimationForStatusImage:self.iviewQoSReceive];
    
    [[AutoReLoginDaemon sharedInstance] setDebugObserver:[self createObserverCompletionForDEBUG:self.iviewAutoRelogin]];
    [[KeepAliveDaemon sharedInstance] setDebugObserver:[self createObserverCompletionForDEBUG:self.iviewKeepAlive]];
    [[QoS4SendDaemon sharedInstance] setDebugObserver:[self createObserverCompletionForDEBUG:self.iviewQoSSend]];
    [[QoS4ReciveDaemon sharedInstance] setDebugObserver:[self createObserverCompletionForDEBUG:self.iviewQoSReceive]];
}

- (void) setupAnimationForStatusImage:(UIImageView *)iv
{
    iv.animationImages = [NSArray arrayWithObjects:
                          [UIImage imageNamed:@"green_light.png"],
                          [UIImage imageNamed:@"green.png"],
                          nil];
    iv.animationDuration = 0.5;
    iv.animationRepeatCount = 1;
}

- (ObserverCompletion) createObserverCompletionForDEBUG:(UIImageView *)iv
{
    ObserverCompletion clp = ^(id observerble ,id data) {
        int status = [(NSNumber *)data intValue];
        [self showDebugStatusImage:status forImageView:iv];
    };
    
    return clp;
}

- (void) showDebugStatusImage:(int)status forImageView:(UIImageView *)iv
{
    if(iv.hidden)
        iv.hidden = NO;
    if(status == 1)
    {
        // 确保先stop ，否则正在动画中时此时设置图片则只会停在动画的最后一帧
        if([iv isAnimating])
            [iv stopAnimating];
        [iv setImage:[UIImage imageNamed:@"green.png"]];
    }
    else if(status == 2)
    {
        [iv setImage:[UIImage imageNamed:@"green.png"]];
        if([iv isAnimating])
            [iv stopAnimating];
        [iv startAnimating];
    }
    else
    {
        // 确保先stop ，否则正在动画中时此时设置图片则只会停在动画的最后一帧
        if([iv isAnimating])
            [iv stopAnimating];
        [iv setImage:[UIImage imageNamed:@"gray.png"]];
    }
}
//=============================================================================== just for debug END

//=============================================================================== 有关主界面表格的托管实现方法 START
#pragma mark - Table view delegate

// 根据显示内容计算行高
- (CGSize)_calculateCellSize:(NSIndexPath *)indexPath
{
    // 列寬
    CGFloat contentWidth = self.tableView.frame.size.width;
    if(self.chatInfoList == nil)
        return CGSizeMake(contentWidth, 16);
    ChatInfoTableViewCellDTO * item = [self.chatInfoList objectAtIndex:indexPath.section];
    
    // 用何種字體進行顯示
    //### Bug FIX: 此字号设为12时，在iPhone5C(iOS7.0(11A466))真机上会出现字体显示不全的bug（下偏），
    //              但其它真机包括模拟器上却不会，难道是iOS7.0的bug？-- By Jack Jiang 2015-09-16
    UIFont *font = [UIFont systemFontOfSize:14];
    // 該行要顯示的內容
    NSString *content = item.content;
    // 計算出顯示完內容需要的最小尺寸
    CGSize size = [content sizeWithFont:font constrainedToSize:CGSizeMake(contentWidth, 1000) lineBreakMode:NSLineBreakByCharWrapping];
    
    // NSLog(@"-------计算出的高度=%f", size.height);
    
    return size;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.chatInfoList == nil?0:[self.chatInfoList count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self _calculateCellSize:indexPath].height;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.chatInfoList == nil)
        return nil;
    
    ChatInfoTableViewCellDTO * item = [self.chatInfoList objectAtIndex:indexPath.section];
    
    // 表格单元可重用ui
    static NSString *idenfity=@"Cell";
    ChatInfoTableViewCell * cell=[tableView dequeueReusableCellWithIdentifier:idenfity];
    if(cell==nil) {
        NSArray* arr = [[NSBundle mainBundle] loadNibNamed:@"ChatInfoTableViewCell" owner:self options:nil];
        for (id obj in arr) {
            if ([obj isKindOfClass:[ChatInfoTableViewCell class]]) {
                cell = (ChatInfoTableViewCell*)obj;
            }
        }
    }
    
    // 利表格单元对应的数据对象对ui进行设置
    cell.lbContent.text = item.content;
    NSInteger colorType = item.colorType;
    UIColor *cellColor = nil;
    switch(colorType)
    {
        case TABLE_CELL_COLOR_BLUE:
            cellColor = [UIColor colorWithRed:0/255.0f green:0/255.0f blue:255/255.0f alpha:1];
            break;
        case TABLE_CELL_COLOR_BRIGHT_RED:
            cellColor = [UIColor colorWithRed:255/255.0f green:0/255.0f blue:255/255.0f alpha:1];
            break;
        case TABLE_CELL_COLOR_RED:
            cellColor = [UIColor colorWithRed:255/255.0f green:0/255.0f blue:0/255.0f alpha:1];
            break;
        case TABLE_CELL_COLOR_GREEN:
            cellColor = [UIColor colorWithRed:0/255.0f green:128/255.0f blue:0/255.0f alpha:1];
            break;
        case TABLE_CELL_COLOR_BLACK:
        default:
            cellColor = [UIColor colorWithRed:0/255.0f green:0/255.0f blue:0/255.0f alpha:1];
            break;
    }
    if(cellColor != nil)
        cell.lbContent.textColor = cellColor;
    
    // ** 设置cell的lable高度
    CGRect rect = [cell.textLabel textRectForBounds:cell.textLabel.frame limitedToNumberOfLines:0];
    // 設置顯示榘形大小
    rect.size = [self _calculateCellSize:indexPath];
    // 重置列文本區域
    cell.lbContent.frame = rect;
    
    return cell;
}

// 点击表格行时要调用的方法
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // do nothing
    
    // 自动取消选中状态，要不然看起来很丑
    [self performSelector:@selector(deselectTableViewCell) withObject:nil afterDelay:0.5f];
}

- (void)deselectTableViewCell
{
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    
}
//=============================================================================== 有关主界面表格的托管实现方法 END

@end
