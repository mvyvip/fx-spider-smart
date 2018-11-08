var  element = layui.element;
window.tab = {
    add: function (id, title, url, icon) {
        if (!$("[lay-id*=" + id + "]")[0]) {
            element.tabAdd("main_tab", {
                title: "<i class='" + icon + "'> " + title + "</i>",
                content: "<iframe onload='setIframeHeight(this)' src='" + url + "' style='width:100%;' frameborder='0'  />",
                id: id
            })
        }
        element.tabChange("main_tab", id);
    }
}

function initTab(cols, url) {
    var index = layer.load(2);
    table.render({
        id: 'main_tab',
        page: true,
        limits: [15, 30, 50],
        limit: 15,
        elem: '#main_tab',
        cols: cols,
        url: url,
        done: function(res, curr, count){
            layer.close(index);
        }
    });
    listen(url);
}


function listen(url) {
    table.on('tool(main_tab)', function(obj){
        var id = obj.data.id;
        var event = obj.event;
        if(event === 'del'){
            del(url, id);
        } else if(event === 'edit'){
            $.get({
                url: url + '/' + id,
                success: function (data) {
                    layer.open({
                        type: 1,
                        title: '操作',
                        area:'800px',
                        offset: '100px',
                        content: data
                    });
                }
            })
        }else if(event === 'parameters'){
            window.location.href = '/design/itemParameters/list?itemId='+id;
        }
    });
}