#lab3 LittleMylyn
## 使用说明
### 打开方式
插件主要有两种打开方式<br>
1. Windows -> Show View -> Other -> LittleMylyn<br>
2. <br>
<br>

### 新建Task
1. 在LittleMylyn的视图范围内，右键，会出现New Task按钮，单击即可。<br>
2. 单击之后会有向导页，引导用户为Task命名，设置Task状态和类别<br>
<br>

### 更改Task状态
1. 双击希望修改的状态，会出现修改Task状态的向导页，只有两个选项：Finished和Activated，因为任何Task不允许被修改为New。<br>
2. 当一个Finished的Task被修改为Activated，之前状态为Activated的Task的状态会被默认修改为Finished，从而保证每一时刻最多只有一个Task处于Activated状态。<br>
<br>

### 打开关联文件
1. 在RelatedClasses目录下双击文件名即可打开此文件。<br>
<br>

### 显示细节
所有节点按照字母序排列

## 功能说明
1. 打开一个文件，如果当前有一个Task处于Activated状态，则此文档名及其路径会在此Task的RelatedClasses显示。
2. 新建一个文件，此文件也会被关联到处于Activated状态下的Task下。
3. 删除一个文件，无论Task是否处于Activated状态，只要其RelatedClasses下包含有此文件，均会删除
4. 重命名一个文件，无论Task是否处于Activated状态，只要其RelatedClasses下包含有此文件，均会重命名
