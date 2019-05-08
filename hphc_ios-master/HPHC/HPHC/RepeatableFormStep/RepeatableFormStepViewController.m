/*
 License Agreement for FDA My Studies
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

#import "RepeatableFormStepViewController.h"
#import "RepeatableFormStep.h"

@interface ORKFormStepViewController (Internal)

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView;



- (void) stepDidChange;

@end


@interface RepeatableFormStepViewController ()

@property (nonatomic, strong) NSArray *originalFormItems;

@property(nonatomic, weak) UITableView *originalTableView;

@property (nonatomic, assign) NSInteger repeatableTextSection;

@property (nonatomic, assign) NSInteger lastSectionRowCount;

@end

@implementation RepeatableFormStepViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSInteger originalItemCount = [[self formStep] initialItemCount];
    NSMutableArray *items = [[[self formStep] formItems] mutableCopy];
    [items removeLastObject];
    if (items.count > originalItemCount) {
        [items removeObjectsInRange:NSMakeRange(originalItemCount, items.count - originalItemCount)];
    }
    _originalFormItems = items;
    
    
}

- (void) viewDidAppear:(BOOL)animated {
    [super viewDidAppear: animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (RepeatableFormStep *)formStep {
    NSAssert(!self.step || [self.step isKindOfClass:[RepeatableFormStep class]], nil);
    return (RepeatableFormStep *)self.step;
}


- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section {
    
    if (![self formStep].repeatable) {
        return;
    }
    
    _originalTableView = tableView;
    
    NSInteger sections = [super numberOfSectionsInTableView:tableView];
    
    NSLog(@"sections-> %ld", (long)sections);

    if (sections - 1 == section) {
        if ([view.subviews.lastObject isKindOfClass:[UIButton class]]) {
            return;
        }
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        
        //button.frame = view.bounds;
        
        
        [button setTitle:[self formStep].repeatableText forState:UIControlStateNormal];
        
        
        
        
        UIFont *font1 = [UIFont fontWithName:@"HelveticaNeue-Medium" size:16.0f];
       
        NSDictionary *dict1 = @{NSUnderlineStyleAttributeName:@(NSUnderlineStyleSingle),
                                NSFontAttributeName:font1,
                                NSForegroundColorAttributeName:[UIColor colorWithRed: 0.0f / 255.0f green: 124.0f / 255.0f blue: 186.0f / 255.0f alpha:1]}; // Added line
        
        
        NSMutableAttributedString *attString = [[NSMutableAttributedString alloc] init];
        [attString appendAttributedString:[[NSAttributedString alloc] initWithString:[self formStep].repeatableText    attributes:dict1]];
       
        
        CGSize buttonSize = [[self formStep].repeatableText sizeWithAttributes:dict1];
        
        
        CGFloat x = (view.bounds.size.width - (buttonSize.width + 10)) / 2   ;
        
        button.frame =  CGRectMake((x > 0 ? x : 0), view.bounds.origin.y, buttonSize.width + 10.0f, buttonSize.height);
        
        [button setAttributedTitle:attString forState:UIControlStateNormal];
       
        [button addTarget:self action:@selector(addMoreAction:) forControlEvents:UIControlEventTouchUpInside];
        [view addSubview:button];
        
        _repeatableTextSection = section;
        
        _lastSectionRowCount = [_originalTableView numberOfRowsInSection:section - 1];
      NSLog(@"last section row Count: %ld",(long)_lastSectionRowCount);
    }
}

- (void)tableView:(UITableView* )tableView willDisplayCell:(UITableViewCell* )cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    _originalTableView = tableView;
}

- (void) addMoreAction:(id)sender {
    
    NSMutableArray *mItems = [[[self formStep] formItems] mutableCopy];
    [mItems removeLastObject];
    
    NSInteger count = mItems.count ;
    NSInteger suffix = count / _originalFormItems.count ;
    
    for (ORKFormItem *item in _originalFormItems) {
        NSString *identifier = [item.identifier stringByAppendingFormat:@"_%ld", (long)suffix];
        ORKFormItem *mItem;
        if (item.answerFormat != nil) {
            mItem = [[ORKFormItem alloc] initWithIdentifier:identifier text:item.text answerFormat:[item.answerFormat copy] optional:item.optional];
            mItem.placeholder = item.placeholder;
        }
        else {
            mItem = [[ORKFormItem alloc] initWithSectionTitle:item.text];
        }
        [mItems addObject:mItem];

    }

    [[self formStep] setFormItems:mItems];
    [super stepDidChange];
    
   
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        
        NSLog(@"tcs-> %@ %@", NSStringFromCGSize(self.originalTableView.contentSize), NSStringFromCGSize(self.originalTableView.frame.size));

        if (self.originalTableView.contentSize.height > self.originalTableView.frame.size.height)
        {
          
            NSInteger previousLastSectionRowCount = [self.originalTableView numberOfRowsInSection:self.repeatableTextSection - 1];
            
            NSIndexPath *scrollToIndexPath;
            
            if (self.lastSectionRowCount <= previousLastSectionRowCount) {
                scrollToIndexPath = [NSIndexPath indexPathForRow:previousLastSectionRowCount - 1 inSection:self.repeatableTextSection - 1];
            }else {
                scrollToIndexPath = [NSIndexPath indexPathForRow:0 inSection:self.repeatableTextSection];
            }
            
            [self.originalTableView scrollToRowAtIndexPath:scrollToIndexPath atScrollPosition:UITableViewScrollPositionTop animated:YES];
        }
        
    });

}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
