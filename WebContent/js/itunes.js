$(function(){
    var count, getCharts, state, type, url, genre;
    state = "cn";
    type = "topfreeapplications";
    count = 10;
    genre = "";
    getCharts = function(url, callback) {
        return $.getJSON(url, function(data) {
            var bean, json, temp, _i, _len, _ref;
            moment.lang('zh-cn');
            json = {
                updated: moment.utc(data.feed.updated.label).fromNow(),
                entry: []
            };
            _ref = data.feed.entry;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                bean = _ref[_i];
                temp = {};
                temp.name = bean['im:name'].label;
                temp.image = bean['im:image'][0].label;
                temp.price = bean['im:price'].label;
                temp.title = bean['title'].label;
                temp.link = bean['id'].label;
                json.entry.push(temp);
            }
            return callback(json);
        });
    };
    
    var isIe = $.browser.msie ? "?callback=?" : "";
    url = "https://itunes.apple.com/" + state + "/rss/" + type + "/limit=" + count + "/genre=" + genre + "/json" + isIe;
    
    getCharts(url, function(data) {
        $('tfoot .time').text(data.updated);
        var beans = data.entry;
        var bean, _i, _len;
        for (_i = 0, _len = beans.length; _i < _len; _i++) {
            bean = beans[_i];
            bean.id = _i + 1;
            var html = template.render('tableTemp', bean);
            $('table tbody').append(html).hide().show("slow");
        }
    });

    $('select').change(function(){
        if($(this).hasClass('state')) {
            state = $(this).val();
        } else if ($(this).hasClass('type')) {
            type = $(this).val();
        } else if ($(this).hasClass('count')) {
            count = $(this).val();
        } else if ($(this).hasClass('genre')) {
            genre = $(this).val();
        }
        url = "https://itunes.apple.com/" + state + "/rss/" + type + "/limit=" + count + "/genre=" + genre + "/json" + isIe;
        getCharts(url, function(data) {
            $('tfoot .time').text(data.updated);
            var beans = data.entry;
            var bean, _i, _len;
            $('table tbody').html('');
            for (_i = 0, _len = beans.length; _i < _len; _i++) {
                bean = beans[_i];
                bean.id = _i + 1;
                var html = template.render('tableTemp', bean);
                $('table tbody').append(html).hide().show("slow");
            }
        });
    });
});