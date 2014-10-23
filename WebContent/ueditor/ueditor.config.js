/**
 *  ueditor完整配置项
 *  可以在这里配置整个编辑器的特性
 */
(function () {
    /**
     * 编辑器资源文件根路径。它所表示的含义是：以编辑器实例化页面为当前路径，指向编辑器资源文件（即dialog等文件夹）的路径。
     * window.UEDITOR_HOME_URL = "/xxxx/xxxx/";
     */
    var URL = window.UEDITOR_HOME_URL || getUEBasePath();
    var qiniuPath = "http://file.dreamlu.net/";
    window.UEDITOR_CONFIG = {
        //为编辑器实例添加一个路径，这个不能被注释
        UEDITOR_HOME_URL : URL

        //图片上传配置区
        ,imageUrl: URL + "uploadImg"             //图片上传提交地址
        ,imagePath: qiniuPath						 //图片修正地址，引用了fixedImagePath,如有特殊需求，可自行配置
        ,imageFieldName:"upfile"                  //图片数据的key,若此处修改，需要在后台对应文件修改对应参数

        //涂鸦图片配置区
        ,scrawlUrl: URL + "uploadScrawl"          //涂鸦上传地址
        ,scrawlPath: qiniuPath						 //图片修正地址，同imagePath

        //附件上传配置区
        ,fileUrl: URL + "uploadFile"              //附件上传提交地址
        ,filePath: qiniuPath                              //附件修正地址，同imagePath
        ,fileFieldName:"upfile"                   //附件提交的表单名，若此处修改，需要在后台对应文件修改对应参数

        //远程抓取配置区
        ,catchRemoteImageEnable:false               //是否开启远程图片抓取,默认开启
        //,catcherUrl: URL + "getRemoteImage"        //处理远程图片抓取的地址
        //,catcherPath:""                            //图片修正地址，同imagePath
        //,catchFieldName:"upfile"                   //提交到后台远程图片uri合集，若此处修改，需要在后台对应文件修改对应参数
        //,separater:'ue_separate_ue'              //提交至后台的远程图片地址字符串分隔符
        //,localDomain:[]                            
        //本地顶级域名，当开启远程图片抓取时，除此之外的所有其它域名下的图片都将被抓取到本地,默认不抓取127.0.0.1和localhost

        //图片在线管理配置区
        ,imageManagerUrl: URL + "imageManager"      //图片在线管理的处理地址
        ,imageManagerPath: qiniuPath                       //图片修正地址，同imagePath
        ,separater: "," 

        //工具栏上的所有的功能按钮和下拉框，可以在new编辑器的实例时选择自己需要的从新定义
        , toolbars:[
            [
                'fullscreen', 'source', '|', 'undo', 'redo', '|','bold', 'italic', 'underline', 'fontborder', 
                'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 
                'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 
                'selectall', 'cleardoc', '|','rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
                'customstyle', 'paragraph', 'fontfamily', 'fontsize',
                'directionalityltr', 'directionalityrtl', 'indent', '|',
                'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
                'link', 'unlink', 'anchor', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
                'insertimage', 'emotion', 'scrawl', 'insertvideo', 'music', 'attachment', '|', 'map', 'gmap', 
                'insertframe','insertcode', 'pagebreak', 'template', 'background', '|',
                'horizontal', 'date', 'time', 'spechars', '|',
                'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 
                'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
                'print', 'preview', 'searchreplace', 'help', 'drafts']
        ]

        ,initialContent:''       //初始化编辑器的内容,也可以通过textarea/script给值，看官网例子
        ,initialFrameWidth:800  //初始化编辑器宽度,默认1000
        ,initialFrameHeight:200  //初始化编辑器高度,默认320
    };

    function getUEBasePath ( docUrl, confUrl ) {
        return getBasePath( docUrl || self.document.URL || self.location.href, confUrl || getConfigFilePath() );

    }

    function getConfigFilePath () {
        var configPath = document.getElementsByTagName('script');
        return configPath[ configPath.length -1 ].src;
    }

    function getBasePath ( docUrl, confUrl ) {
        var basePath = confUrl;
        if ( !/^[a-z]+:/i.test( confUrl ) ) {
            docUrl = docUrl.split( "#" )[0].split( "?" )[0].replace( /[^\\\/]+$/, '' );
            basePath = docUrl + "" + confUrl;
        }
        return optimizationPath( basePath );
    }

    function optimizationPath ( path ) {
        var protocol = /^[a-z]+:\/\//.exec( path )[ 0 ],
            tmp = null,
            res = [];
        path = path.replace( protocol, "" ).split( "?" )[0].split( "#" )[0];
        path = path.replace( /\\/g, '/').split( /\// );
        path[ path.length - 1 ] = "";
        while ( path.length ) {
            if ( ( tmp = path.shift() ) === ".." ) {
                res.pop();
            } else if ( tmp !== "." ) {
                res.push( tmp );
            }
        }
        return protocol + res.join( "/" );
    }

    window.UE = {
        getUEBasePath: getUEBasePath
    };
})();
