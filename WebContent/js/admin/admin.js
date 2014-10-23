$("[rel=tooltip]").tooltip();
$('input').tipsy({gravity: 'w', fade: true, html: true});
$(function() {
    $('.demo-cancel-click').click(function(){return false;});
    // 更改状态
    $(document).on("click", ".delete_show", function(e){
        var dialog = $.dialog();
        $.post( $(this).attr('href'), function(data) {
            if(data.status === 2){
                dialog.content('你没有权限，请联系管理员！').lock().time(2000);
            } else if( data.status ) {
                dialog.content('状态更新成功！').lock().time(1000);
                setTimeout(function(){
                    location.reload();
                }, 1000);
            } else {
                dialog.content('操作失败，请稍后再试！').lock().time(2000);
            }
        });
        return false;
    });
});
function _post(dom, dialog, cb) {
    var $form = $(dom);
    $.post($form.attr('action')
        , $form.serialize()
        , function(data){
            if (data.status === 1) {
                dialog.content('请检查用户名或密码！').lock().time(2000);
            } else if (data.status === 2) {
                dialog.content('你没有权限，请联系管理员！').lock().time(2000);
            } else {
                cb(data);
            }
        }
    );
}