$(function(){
    $('#otable').dataTable({
        "bLengthChange": true,
        "bFilter": true, //搜索栏
        "bSort": false, //是否支持排序功能
        "bInfo": true, //显示表格信息
        "bStateSave": true, //保存状态到cookie
        "sPaginationType": "full_numbers", //分页，一共两种样式，full_numbers和two_button(默认)
        "bProcessing": true,
        "bServerSide": true,
        "iDisplayLength": 10,
        "oLanguage": {
            "sLengthMenu": "每页显示 _MENU_ 记录",
            "sZeroRecords": "对不起，查询不到任何相关数据",
            "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录",
            "sInfoEmtpy": "找不到相关数据",
            "sInfoFiltered": "(数据表中共为 _MAX_ 条记录)",
            "sProcessing": "正在加载中...",
            "sSearch": "搜索：",
            "aaSorting": [[1, "desc"]],  //第一个参数表示表示Browser列。第二个参数为 desc或是asc
            "oPaginate": {
                "sFirst":    "首页",
                "sPrevious": "上一页 ",
                "sNext":     "下一页 ",
                "sLast":     "尾页 "
            }
        }, //多语言配置
        "sAjaxSource": "/admin/blog/list_json",
        "bJQueryUI": true,
        "aoColumnDefs": [
            {
                "aTargets": [0],
                "mData": null,
                "mRender": function (data, type, full) {
                    return full.id;
                }
            },{
                "aTargets": [1],
                "mData": null,
                "mRender": function (data, type, full) {
                    var title = full.title.length > 35 ? full.title.substr(0, 32) + '...' : full.title;
                    return template.render('titleTemp', {title: title, id: full.id});
                }
            },{
                "aTargets": [2],
                "mData": null,
                "mRender": function (data, type, full) {
                    var types = ['原创', '收藏'];
                    return types[full.blog_type];
                }
            },{
                "aTargets": [3],
                "mData": null,
                "mRender": function (data, type, full) {
                    return template.render('statusTemp', {status: full.del_status});
                }
            },{
                "aTargets": [4],
                "mData": null,
                "mRender": function (data, type, full) {
                    return full.nick_name;
                }
            },{
                "aTargets": [5],
                "mData": null,
                "mRender": function (data, type, full) {
                    return full.update_time.split(' ')[0];
                }
            },{
                "aTargets": [6],
                "mData": null,
                "mRender": function (data, type, full) {
                    return template.render('manageTemp', {id: full.id, status: full.del_status});
                }
            }
        ],
        "aLengthMenu": [[10, 25, 50], ["10条", "25条", "50条"]]  //设置每页显示记录的下拉菜单
    });
});