/**
 * 公共JS方法
 * Created by chencx@zhikucd.com on 2016/5/13.
 */
(function ($) {

    //------------config------------

    //ali oss 地址
    DEV_OSS_BASE_URI = "http://mart.oss-cn-beijing.aliyuncs.com/";
    QA_OSS_BASE_URI = "http://mart.oss-cn-beijing.aliyuncs.com/";
    PRODUCT_OSS_BASE_URI = "http://zzg-mall.oss-cn-shenzhen.aliyuncs.com/";
    OSS_HOSTS_URI = PRODUCT_OSS_BASE_URI;
    OSS_PHOTOS_LAYER_OPEN = '?x-oss-process=image/resize,m_lfit,h_650,w_650,limit_0';


    /**
     * 不支持IE浏览器
     */
    if (checkBrowerType() == "IE") {
        window.location.href = "" + getRootPath() + "/page/browser.html";
    }
    /**
     * 检查浏览器类型
     * @returns {*}
     */
    function checkBrowerType() {
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览器
        var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
        var isSafari = userAgent.indexOf("Safari") > -1; //判断是否Safari浏览器
        if (isIE) {
            var IE5 = IE55 = IE6 = IE7 = IE8 = false;
            var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
            reIE.test(userAgent);
            var fIEVersion = parseFloat(RegExp["$1"]);
            IE55 = fIEVersion == 5.5;
            IE6 = fIEVersion == 6.0;
            IE7 = fIEVersion == 7.0;
            IE8 = fIEVersion == 8.0;
            if (IE55 || IE6 || IE7 || IE8) {
                return "IE";
            }
        }//isIE end
        if (isFF) {
            return "FF";
        }
        if (isOpera) {
            return "Opera";
        }
        //以下是调用上面的函数
        /*if (myBrowser() == "FF") {

         }
         if (myBrowser() == "Opera") {

         }
         if (myBrowser() == "Safari") {

         }*/
    }

    //js获取项目根路径，如： http://localhost:8083/uimcardprj
    function getRootPath() {
        //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
        var curWwwPath = window.document.location.href;
        //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        //获取主机地址，如： http://localhost:8083
        var localhostPaht = curWwwPath.substring(0, pos);
        //获取带"/"的项目名，如：/uimcardprj
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPaht + projectName);
    }

    /**
     * 获取url参数
     * @param name 参数KEY
     * @returns {null}
     */
    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURIComponent(r[2]);
        return null;
    }

    /**
     * form表单初始化
     * @param data 初始化数据
     * @ignoreArray 忽略字段
     * @returns {*}
     */
    $.fn.formatForm = function (data, ignoreArray) {
        return this.each(function () {
            var input, name;
            if (data == null) {
                this.reset();
                return;
            }
            for (var i = 0; i < this.length; i++) {
                input = this.elements[i];
                //checkbox的name可能是name[]数组形式
                if (input.type == 'file') continue;//不填充input ile类型
                if ($.inArray(input.type, ignoreArray) > -1) continue; //需要忽略的name值
                name = (input.type == "checkbox") ? input.name.replace(/(.+)\[\]$/, "$1") : input.name;
                if (data[name] == undefined) continue;
                switch (input.type) {
                    case "checkbox":
                        if (data[name] == "") {
                            input.checked = false;
                        } else {
                            //数组查找元素
                            if (data[name].indexOf(input.value) > -1) {
                                input.checked = true;
                            } else {
                                input.checked = false;
                            }
                        }
                        break;
                    case "radio":
                        if (data[name] == "") {
                            input.checked = false;
                        } else if (input.value == data[name]) {
                            input.checked = true;
                        }
                        break;
                    case "button":
                        break;
                    default:
                        input.value = data[name];
                }
            }
        });
    };


    /**
     * 对Date的扩展，将 Date 转化为指定格式的String
     * @param fmt 日期格式
     * @returns {*}
     * @constructor
     */
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1,                 //月份
            "d+": this.getDate(),                    //日
            "h+": this.getHours(),                   //小时
            "m+": this.getMinutes(),                 //分
            "s+": this.getSeconds(),                 //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }


    // Ajax 文件下载
    $.download = function (url, data, method) {
        // 获取url和data
        if (url && data) {
            // data 是 string 或者 array/object
            data = typeof data == 'string' ? data : jQuery.param(data);
            // 把参数组装成 form的  input
            var inputs = '';
            jQuery.each(data.split('&'), function () {
                var pair = this.split('=');
                inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
            });
            // request发送请求
            jQuery('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
                .appendTo('body').submit().remove();
        }
    };

    $.unixToDate = function (unixTime, isFull, timeZone) {
        if (typeof (timeZone) === 'number') {
            unixTime = parseInt(unixTime) + parseInt(timeZone) * 60 * 60;
        }
        var date = new Date(unixTime);
        var y = date.getFullYear();
        var m = date.getMonth() + 1;
        m = m < 10 ? ('0' + m) : m;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        var h = date.getHours();
        h = h < 10 ? ('0' + h) : h;
        var minute = date.getMinutes();
        var second = date.getSeconds();
        minute = minute < 10 ? ('0' + minute) : minute;
        second = second < 10 ? ('0' + second) : second;
        return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;
    };

    /**
     * Input邮箱匹配
     * @param settings
     * @returns {*}
     */
    $.emailMatcher = function () {
        return function findEmailDomainMatches(queryString, cb) {
            var emailDomains = [
                'qq.com',
                '163.com',
                '126.com',
                'gmail.com',
                'hotmail.com',
                'outlook.com',
                'icloud.com',
                'foxmail.com',
                'sina.com',
                'sina.cn',
                '139.com',
                '139.cn'
            ];
            var matches = [];
            var atPosition = queryString.indexOf('@');

            if (atPosition < 0) {
                $.each(emailDomains, function (i, domain) {
                    matches.push({value: queryString + '@' + domain});
                });
            } else if (atPosition > 0) {
                var username = queryString.substr(0, atPosition);
                var queryDomain = queryString.substr(atPosition + 1);

                var matchWholeDomain = false;
                var allRegex = new RegExp('^' + queryDomain + '$', 'i');
                $.each(emailDomains, function (i, domain) {
                    if (allRegex.test(domain)) {
                        matchWholeDomain = true;
                        return false;
                    }
                });
                if (matchWholeDomain) {
                    return cb(matches);
                }

                var matchStart = [];
                var matchAny = [];
                var startRegex = new RegExp('^' + queryDomain, 'i');
                var anyRegex = new RegExp(queryDomain, 'i');
                $.each(emailDomains, function (i, domain) {
                    if (startRegex.test(domain)) {
                        matchStart.push({value: username + '@' + domain});
                    } else if (anyRegex.test(domain)) {
                        matchAny.push({value: username + '@' + domain});
                    }
                });
                matches = matchStart.concat(matchAny);
            } else {

            }
            cb(matches.slice(0, 8));
        };
    };


    /******************************************************************************
     * DataTables 公共方法
     * 2016/10/19 chencunxin
     *******************************************************************************/


    /**
     * DATATABLE 常量
     * by:chencx@zhikucd.com
     */
    CONSTANT = {
        DATA_TABLES: {
            DEFAULT_OPTION: { // DataTables初始化选项
                LANGUAGE: {
                    "sProcessing": "处理中...",
                    "sLengthMenu": "每页 _MENU_ 项",
                    "sZeroRecords": "没有匹配结果",
                    "sInfo": "当前显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项。",
                    "sInfoEmpty": "当前显示第 0 至 0 项，共 0 项",
                    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                    "sInfoPostFix": "",
                    "sSearch": "搜索:",
                    "sUrl": "",
                    "sEmptyTable": "表中数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands": ",",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sPrevious": "上页",
                        "sNext": "下页",
                        "sLast": "末页",
                        "sJump": "跳转"
                    },
                    "oAria": {
                        "sSortAscending": ": 以升序排列此列",
                        "sSortDescending": ": 以降序排列此列"
                    }
                },
                // 禁用自动调整列宽
                autoWidth: false,
                // 为奇偶行加上样式，兼容不支持CSS伪类的场合
                stripeClasses: ["odd", "even"],
                // 取消默认排序查询,否则复选框一列会出现小箭头
                order: [],
                // 隐藏加载提示,自行处理
                processing: false,
                // 启用服务器端分页
                serverSide: true,
                // 禁用原生搜索
                searching: false
            },
            COLUMN: {
                // 复选框单元格
                CHECKBOX: {
                    className: "td-checkbox",
                    orderable: false,
                    bSortable: false,
                    data: "id",
                    render: function (data, type, row, meta) {
                        var content = '<label style="text-align: center" class="mt-checkbox mt-checkbox-single mt-checkbox-outline">';
                        content += '	<input type="checkbox" class="group-checkable" value="' + data + '" />';
                        content += '	<span></span>';
                        content += '</label>';
                        return content;
                    }
                }
            },
            // 常用render可以抽取出来，如日期时间、头像等
            RENDER: {
                ELLIPSIS: function (data, type, row, meta) {
                    data = data || "";
                    return '<span title="' + data + '">' + data + '</span>';
                }
            }
        }
    };


    /**
     * DataTables Ajax 数据绑定
     * @param data DataTables data
     * @param callback DataTables 回调渲染方法
     * @param param 传入后台参数
     * @param method 请求方式
     * @param url 请求地址
     * @param settings DataTables settings
     */
    $.dataTableAjax = function (data, callback, param, method, url, settings) {
        //封装请求参数
        param.limit = data.length;//页面显示记录条数，在页面显示每页显示多少项的时候
        param.offset = ((data.start / data.length) + 1);//当前页码
        //ajax请求数据
        $.ajax({
            type: method,
            url: url,
            cache: false, //禁用缓存
            data: param, //传入组装的参数
            dataType: "json",
            success: function (result) {
                //封装返回数据
                var returnData = {};
                returnData.draw = data.draw;//这里直接自行返回了draw计数器,应该由后台返回
                returnData.recordsTotal = result.total;//返回数据全部记录
                returnData.recordsFiltered = result.total;//后台不实现过滤功能，每次查询均视作全部结果
                returnData.data = result.pageData;//返回的数据列表
                //调用DataTables提供的callback方法，代表数据已封装完成并传回DataTables进行渲染
                //此时的数据需确保正确无误，异常判断应在执行此回调前自行处理完毕
                callback(returnData);
            }
        });
    }

    /**
     * Form表单序列化成Object对象
     * @returns {{}}
     */
    $.fn.serializeObject = function () {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

})(jQuery);