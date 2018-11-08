var $ = layui.jquery;
var form = layui.form;
var table = layui.table;

window.msg = function (msg) {
    layer.msg(msg ? msg : "操作成功");
}

window.error = function(msg) {
    layer.msg(msg ? msg : "未知异常,请联系管理员", {
        time: 1500,
        icon: 2
    });
}

window.confirm = function (msg, call) {
    layer.confirm(msg, {icon: 3, title:'温馨提示'}, call);
}

/**
 * 弹出层
 * @param url  form地址
 */
function add(url) {
    $.get({
        url: url,
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
}

/**
 * 批量删除  table.render设置的id必须为main_tab
 * @param url
 * @param id  如果不传就自动寻找选中的数据
 */
function del(url, id) {
    var datas = table.checkStatus('main_tab').data;
    if(id == null && datas.length < 1) {
        msg("最少选择一条数据");
        return;
    }
    confirm('确定删除吗?', function(index){
        layer.close(index);
        var ids = new Array();
        if(id != null) {
            ids.push(id);
        } else {
            layui.each(datas, function(index){
                ids.push(datas[index].id);
            });
        }
        $.ajax({
            url: url + "/" + ids.join(","),
            type: "DELETE",
            success: function(){
                layer.msg("操作成功");
                table.reload('main_tab', {
                    url: url
                });
            },
            error: function (data) {
                error(data.msg)
            }
        })
    })
}

function serch(url) {
    var options = $("#serch-data").serialize();
    console.log(options)
    table.reload('main_tab', {
        url: url + "?" + options
    })
}

function reload(url, params) {
    table.reload('main_tab', {
        url: url + (typeof(params) != "undefined" ?　"?" + params : "")
    })
}

function formListener(url) {
    form.render();
    form.on('submit(submit-btn)', function(data){
        var  params = data.field;
        layui.jquery.ajax({
            type: "POST",
            url: url,
            data: params,
            success: function () {
                layer.closeAll();
                layer.msg("操作成功");
                table.reload('main_tab', {
                    url: url
                    ,where: {}
                });
            },
            error: function (data) {
                console.log(data);
            }
        })
        return false;
    });
}

function initSelect(url, selectId, id) {
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        success: function (datas) {
            $(datas).each(function () {
                $(selectId).append("<option " + (id == this.id ? "selected" : "") + " value = " + this.id + ">" + this.name + "</option>");
            })
            form.render('select');
        }
    })
}


/**
 * 上面的没加code  这里支持code
 */
function initSelect2(url, selectId, id) {
    $.ajax({
        url: url,
        type: "GET",
        dataType: "json",
        success: function (result) {
            console.log(result)
            if(result.code == 200) {
                $(result.data).each(function () {
                    $(selectId).append("<option " + (id == this.id ? "selected" : "") + " value = " + this.id + ">" + this.name + "</option>");
                })
                form.render('select');
            } else {
                error(result.message)
            }
        }
    })
}