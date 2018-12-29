/*!
 * SuperSlide v2.1.2
 * 轻松解决网站大部分特效展示问题
 * 详尽信息请看官网：http://www.SuperSlide2.com/
 *
 * Copyright 2011-2015, 大话主席
 *
 * 请尊重原创，保留头部版权
 * 在保留版权的前提下可应用于个人或商业用途

 * v2.1.1：修复当调用多个SuperSlide，并设置returnDefault:true 时返回defaultIndex索引错误
 * v2.1.2：增加参数设置vis:"auto"，解决左滚动自适应窗口宽度问题。适应情况：vis:"auto",scroll:1, effect:"left或leftLoop"（注：此为临时解决方案，日后版本可能变动）
 * v2.1.2：修复 mouseOverStop 和 autoPlay均为false下，点击切换按钮后会自动播放bug

 */

function timeChangetype(e) {
    return Date.parse(new Date(e))
}

function GetDateStr(e) {
    var t = new Date;
    return t.setDate(t.getDate() + e), t.getFullYear() + "-" + (t.getMonth() + 1 < 10 ? "0" + (t.getMonth() + 1) : t.getMonth() + 1) + "-" + (t.getDate() < 10 ? "0" + t.getDate() : t.getDate())
}

function setStorage(e, t) {
    e && ("string" != typeof t && (t = JSON.stringify(t)), window.sessionStorage.setItem(e, t))
}

function getStorage(e) {
    if (e) return window.sessionStorage.getItem(e)
}

function removeStore(e) {
    if (e) return window.sessionStorage.removeItem(e)
}

function setLocalStorage(e, t) {
    e && ("string" != typeof t && (t = JSON.stringify(t)), window.localStorage.setItem(e, t))
}

function getLocalStorage(e) {
    if (e) return window.localStorage.getItem(e)
}

function removeLocalStore(e) {
    e && window.localStorage.removeItem(e)
}

function noChoseCity(e, t, a, i) {
    i = i || "请选择出发城市", a = a || "347px", t = t || "0", e.parent().parent().append('<div class="tooltip-error" data-id=' + e.attr("id") + ' style="left:' + a + "; top: " + t + '; display: block;"><i class="icon icon-plaint-fill"></i>' + i + "</div>")
}

function footerFn() {
    $(".content").css("height", "auto");
    var e = $(window).height(),
        t = $(".footer").height(),
        a = $(".content").height(),
        i = e - 109 - t;
    a <= i && $(".content").height(i)
}

function getUrlParms(e) {
    var t = new RegExp("(^|&)" + e + "=([^&]*)(&|$)"),
        a = window.location.search.substr(1).match(t);
    return null != a ? unescape(a[2]) : null
}

function deepClone(e) {
    var t = JSON.stringify(e);
    return JSON.parse(t)
}

function getScSnameListFn() {
    $.ajax({
        url: getScSnameListpr,
        type: "POST",
        timeout: 1e4,
        dataType: "json",
        data: {
            station_telecode: $("#sale").val()
        },
        success: function(e) {
            var t = e.data;
            if ($(".content").height(""), $(".sale-list").empty(), e.data) {
                $("#ticket-box-tips").show();
                for (var a = 0; a < t.length; a++)
                    if (citys[t[a]]) {
                        var i = '<li><h3 class="sale-tit">' + t[a] + '</h3><div class="sale-time">' + (citys[t[a]] || "暂无") + "</div></li>";
                        $(".sale-list").append(i), $(".result-none").hide()
                    }
            } else if (citys[$("#saleText").val()]) {
                var i = '<li><h3 class="sale-tit">' + $("#saleText").val() + '</h3><div class="sale-time">' + (citys[$("#saleText").val()] || "暂无") + "</div></li>";
                $(".sale-list").append(i), $("#ticket-box-tips").show(), $(".result-none").hide()
            } else $(".sale-list").empty(), $("#ticket-box-tips").hide(), $(".result-none").show();
            footerFn()
        },
        error: function(e) {}
    })
}

function loadingShow() {
    $(".mask").fadeIn()
}

function loadingHide() {
    $(".mask").fadeOut()
}
define("g/g-header", ["jquery"], function(e) {
    function t() {
        function t(e) {
            e[0].indexOf(e[1]) > -1 && (e[0] = e[0].replace(e[1], '<span style="color:red;">' + e[1] + "</span>"));
            var t = e[2];
            return e[4] += "<li url=" + e[3] + '><i class="icon icon-' + t + ' "> </i>' + e[0] + '<span class="list-txt"></span></li>', resList = e[4], resList
        }

        function i(a) {
            e.ajax({
                url: getSearchUrl,
                method: "GET",
                timeout: 1e4,
                data: {
                    keyword: a,
                    suorce: "",
                    action: ""
                },
                dataType: "jsonp",
                xhrFields: {
                    withCredentials: !0
                },
                crossDomain: !0,
                success: function(i) {
                    var s = JSON.stringify(i.data);
                    localStorage.setItem("common_search_firstData", s), h = localStorage.getItem("common_search_firstData");
                    var d = i.data.length;
                    if (0 == d) {
                        for (var f = [{
                            value: "城市",
                            ico: "place"
                        }, {
                            value: "车票",
                            ico: "jianpiao"
                        }, {
                            value: "正晚点",
                            ico: "time"
                        }, {
                            value: "起售时间",
                            ico: "selltime"
                        }, {
                            value: "检票口",
                            ico: "jianpiao"
                        }, {
                            value: "时刻表",
                            ico: "date"
                        }, {
                            value: "代售点",
                            ico: "train"
                        }, {
                            value: "交通查询",
                            ico: "zhanche"
                        }, {
                            value: "天气",
                            ico: "weather"
                        }, {
                            value: "问答",
                            ico: "wenda"
                        }, {
                            value: "服务",
                            ico: "fuwu"
                        }, {
                            value: "订单",
                            ico: "dingdanchaxun"
                        }], d = f.length, p = "", v = 0; v <= d - 1; v++) p += '<li><i class="icon icon-' + f[v].ico + ' "> </i>' + f[v].value + '<span class="list-txt"></span></li>';
                        return e(".search-down-list").html(p), e(".search-down").fadeIn(), void(l = "noresults")
                    }
                    for (var _ = "", v = 0; v <= d - 1; v++)
                        if ("001" == i.data[v].type) {
                            var m = "huochepiao";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("view" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("002" == i.data[v].type) {
                            var m = "selltime";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("003" == i.data[v].type) {
                            var m = "time";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("004" == i.data[v].type) {
                            var m = "selltime";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("006" == i.data[v].type) {
                            var m = "yupiao";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("100" == i.data[v].type) {
                            var m = "train";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("101" == i.data[v].type) {
                            var m = "huochepiao";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("102" == i.data[v].type) {
                            var m = "dingdanchaxun";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("103" == i.data[v].type) {
                            var m = "dingdanchaxun";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("104" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("105" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("106" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("107" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("108" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("109" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("110" == i.data[v].type) {
                            var m = "dingcan";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("111" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("112" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("113" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("114" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("115" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("116" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("117" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("118" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("119" == i.data[v].type) {
                            var m = "dingdanchaxun";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("120" == i.data[v].type) {
                            var m = "xiangdao";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("121" == i.data[v].type) {
                            var m = "shanglv";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("122" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("123" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("124" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("125" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("126" == i.data[v].type) {
                            var m = "wenda";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("127" == i.data[v].type) {
                            var m = "dingdanchaxun";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("128" == i.data[v].type) {
                            var m = "dingcan";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("129" == i.data[v].type) {
                            var m = "fuwu";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("130" == i.data[v].type) {
                            var m = "user";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        } else if ("131" == i.data[v].type) {
                            var m = "dingdanchaxun";
                            _ = t([i.data[v].word, a, m, i.data[v].url, _])
                        }
                    e(".search-down-list").html(_), e(".search-down").fadeIn(), e(".search-down-list").off("click", "li").on("click", "li", function() {
                        var t = {
                            innerText: e(this)[0].innerText,
                            url: e(this)[0].getAttribute("url")
                        };
                        if ("" != t.url && void 0 != t.url && null != t.url) {
                            window.open(e(this).attr("url")), c.unshift(t);
                            var a = c.slice(0, 10);
                            n("searchHistory", JSON.stringify(a), 60), o = JSON.parse(r("searchHistory")), u = o;
                            for (var i = "", s = 0; s <= u.length - 1; s++) i += "<li url=" + u[s].url + ">" + u[s].innerText + "</li>";
                            e(".search-history-list").html(i)
                        }
                    })
                },
                error: function(e) {}
            })
        }

        function r(e) {
            var t = document.cookie.indexOf(e),
                a = document.cookie.indexOf(";", t);
            return -1 == t ? "" : unescape(document.cookie.substring(t + e.length + 1, a > t ? a : document.cookie.length))
        }

        function n(e, t, a, i, r, n) {
            var o = document.domain;
            o = o.substring(o.indexOf(".") + 1, o.length);
            var s = new Date;
            s.setTime(s.getTime() + 1e3 * a), document.cookie = escape(e) + "=" + escape(t) + (i ? "; path=" + i : ";path=/") + "; domain=" + o + (n ? "; secure" : "") + ";expires=" + s
        }
        jQuery.support.cors = !0;
        var o, s, l, c = [],
            u = [],
            d = !0;
        e(".header-search .search-input").on("focus", function() {
            if (d = !0, f.splice(0, f.length), e(this).addClass("focus"), e(".search-btn").css({
                background: "#2676E3"
            }), e(".search-down").fadeOut(), e(".search-input").val(""), "" == e(".search-input").val() && (b = 0), r("searchHistory"))
                if (o = JSON.parse(r("searchHistory")), u = o, c = u, 0 != u.length) {
                    for (var t = "", a = 0; a <= u.length - 1; a++) t += "<li url=" + u[a].url + ">" + u[a].innerText + "</li>";
                    e(".search-history-list").html(t), e(".search-history").fadeIn()
                } else e(".search-history").fadeOut();
            else "" != u ? e(".search-history").fadeIn() : e(".search-history").fadeOut();
            e(".search-btn")[0].onclick = function() {
                var t = e(".header-search .search-input").val();
                if (t = t.replace(/^ +| +$/g, ""), !(t.length <= 0)) {
                    for (var a = e(".search-input").val(), h = "[@`~!#$^&*()=|{}':;',\\[\\].<>《》/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]‘’", f = a.length, p = 0; p <= f - 1; p++)
                        if (h.indexOf(a[p]) > -1) return;
                    1 == d && i(a);
                    var v = e(".search-down-list li");
                    if ("noresults" == l);
                    else {
                        if (0 == v.length) return;
                        window.open(v.eq(0).attr("url"))
                    }
                    var _ = {
                        innerText: a,
                        url: v.eq(0).attr("url")
                    };
                    c.unshift(_), s = c.slice(0, 10), n("searchHistory", JSON.stringify(s), 60), o = JSON.parse(r("searchHistory")), u = o, e(".search-input").val("");
                    for (var m = "", p = 0; p <= u.length; p++) {
                        for (var p = 0; p <= u.length - 1; p++) m += "<li url=" + u[p].url + ">" + u[p].innerText + "</li>";
                        e(".search-history-list").html(m)
                    }
                }
            }, e(".search-history-list")[0].onclick = function(t) {
                var t = t || window.event,
                    a = t.target || t.srcElement;
                if ("li" === a.nodeName.toLowerCase()) {
                    if ("undefined" == a.getAttribute("url")) return;
                    e(".search-down-list li");
                    window.open(a.getAttribute("url"))
                }
            }, e(".history-clear").on("click", function() {
                c.splice(0, c.length), s = c.slice(0, 10), n("searchHistory", JSON.stringify(s), 60), o = JSON.parse(r("searchHistory")), u = o, list = "", e(".search-history-list").html(u)
            })
        });
        var h, f = [];
        if (navigator.userAgent.indexOf("Trident") > -1) {
            var p = (navigator.appName, navigator.appVersion),
                v = p.split(";"),
                _ = v && v.length > 1,
                m = _ ? v[1].replace(/[ ]/g, "") : "";
            document.onmousedown = function(t) {
                var t = t || window.event;
                if ("MSIE8.0" == m || "MSIE9.0" == m || "MSIE10.0" == m || "WOW64" == m) {
                    var a = t.clientX,
                        i = t.clientY,
                        r = e("#search-input").offset().left,
                        n = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth, r + e("#search-input").outerWidth()),
                        o = e("#search-input").offset().top,
                        s = e("#search-input").outerHeight(),
                        l = o + s + 204;
                    (a < r || a > n || i < o || i > l) && (e(".search-down").fadeOut(), e(".search-history").fadeOut(), f.splice(0, f.length))
                }
            }
        } else e(".header-search .search-input").on("blur", function() {
            e(".search-history").fadeOut(), e(this).removeClass("focus"), e(".search-btn").css({
                "background-color": "#3B99FC"
            }), e(".search-down").fadeOut(), f.splice(0, f.length)
        });
        var g, y, w, b = 0;
        e(".header-search .search-input").on("keyup", function(t) {
            function a(e) {
                void 0 !== e && "" !== e && window.open(e)
            }
            8 == t.keyCode && (b = 0), e(".search-history").fadeOut();
            g = t.timeStamp, 16 != t.keyCode && 38 != t.keyCode && 40 != t.keyCode && 37 != t.keyCode && 39 != t.keyCode && setTimeout(function() {
                try {
                    if (g - t.timeStamp == 0) {
                        k = e(".search-input").val().toUpperCase(), "" == k && (e(".search-down-list").html(""), e(".search-down").fadeOut()), f.push(k);
                        for (var a = "[@`~!#$^&*()=|{}':;',\\[\\].<>《》/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]‘’", r = k.length, n = 0; n <= r - 1; n++) {
                            if (a.indexOf(k[n]) > -1) return e(".search-down-list").html(""), e(".search-down").fadeOut(), void(d = !1);
                            d = !0
                        }
                        var o = f.length;
                        if ("" != k)
                            if (k.indexOf(f[o - 2]) > -1) {
                                e(".search-down-list").html(""), y = JSON.parse(h), w = y.length;
                                for (var s = "", l = 0, n = 0; n <= w - 1; n++)
                                    if (y[n].word.indexOf(k) > -1 && "001" == y[n].type) {
                                        l++, y[n].word = y[n].word.replace(k, '<span style="color:red;">' + k + "</span>");
                                        s += "<li url=" + y[n].url + '><i class="icon icon-huochepiao "> </i>' + y[n].word + '<span class="list-txt"></span></li>'
                                    }
                                0 == l && 1 == d && i(k), e(".search-down-list").html(s)
                            } else 1 == d && i(k)
                    }
                } catch (t) {
                    k = e(".search-input").val().toUpperCase();
                    for (var a = "[@`~!#$^&*()=|{}':;',\\[\\].<>《》/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]‘’", r = k.length, n = 0; n <= r - 1; n++) {
                        if (a.indexOf(k[n]) > -1) return e(".search-down-list").html(""), e(".search-down").fadeOut(), void(d = !1);
                        d = !0
                    }
                    "" != k && 1 == d && i(k)
                }
            }, 500);
            var l = e(".search-down-list li");
            if (1 == b && 40 != t.keyCode && (b = 0), 40 == t.keyCode && b <= l.length - 1) {
                b++;
                for (var p = 0; p <= l.length - 1; p++) l.eq(p).css({
                    background: "",
                    color: "black"
                }), l.eq(p).children().eq(0).css({
                    color: "#3B99FC"
                });
                if (l.eq(b - 1).css({
                    background: "#3B99FC",
                    color: "white"
                }), l.eq(b - 1).children().eq(0).css({
                    color: "white"
                }), e("#search-input").val(l.eq(b - 1)[0].innerText), b >= 0 && b < 7) e(".search-down-list").scrollTop(0);
                else if (6 != b && parseInt(b / 6) >= 1) {
                    var v = parseInt(b / 6) + 1,
                        _ = 204 * (v - 1) - 30;
                    e(".search-down-list").scrollTop(_)
                }
                l.eq(b - 1).click(function() {
                    window.open(l.eq(b - 1).attr("url"))
                })
            }
            if (38 == t.keyCode && b > 0) {
                b--;
                for (var p = 0; p <= l.length - 1; p++) l.eq(p).css({
                    background: "",
                    color: "black"
                }), l.eq(p).children().eq(0).css({
                    color: "#3B99FC"
                });
                if (l.eq(b - 1).css({
                    background: "#3B99FC",
                    color: "white"
                }), l.eq(b - 1).children().eq(0).css({
                    color: "white"
                }), e("#search-input").val(l.eq(b - 1)[0].innerText), b >= 0 && b < 7) e(".search-down-list").scrollTop(0), 0 == b && (b = 1);
                else if (6 != b && parseInt(b / 6) >= 1) {
                    var v = parseInt(b / 6) + 1,
                        _ = 203.5 * (v - 1) - 30;
                    e(".search-down-list").scrollTop(_)
                }
                l.eq(b - 1).on("click", function() {
                    window.open(l.eq(b).attr("url"))
                })
            }
            if (13 == t.keyCode) {
                var m, D = e(".header-search .search-input").val();
                if (D = D.replace(/^ +| +$/g, ""), D.length <= 0) return;
                var k = e(".search-input").val();
                0 == b ? (a(l.eq(0).attr("url")), m = l.eq(0).attr("url")) : (a(l.eq(b - 1).attr("url")), m = l.eq(b - 1).attr("url"));
                for (var C = {
                    innerText: k,
                    url: m
                }, M = "[@`~!#$^&*()=|{}':;',\\[\\].<>《》/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]‘’", x = k.length, p = 0; p <= x - 1; p++)
                    if (M.indexOf(k[p]) > -1) return;
                c.unshift(C), s = c.slice(0, 10), n("searchHistory", JSON.stringify(s), 60), o = JSON.parse(r("searchHistory")), u = o, e(".search-input").val("")
            }
            for (var T = "", p = 0; p <= u.length - 1; p++) T += "<li url=" + u[p].url + ">" + u[p].innerText + "</li>";
            e(".search-history-list").html(T)
        }), e(".search-down .close").on("click", function() {
            e(".search-input").val(""), e(this).parent().fadeOut(), f.splice(0, f.length)
        }), a()
    }

    function a() {
        var t;
        i(), e.ajax({
            url: loginConf,
            type: "POST",
            timeout: 1e4,
            async: !1,
            success: function(a) {
                a.data && (window.psr_qr_code_result = a.data.psr_qr_code_result, stu_control = a.data.stu_control, other_control = a.data.other_control, "Y" == a.data.is_login ? (t = "Y", window.isLogin = t, window.ajaxLogin_flag = !0, r(), e("#J-header-logout a.txt-primary").html(a.data.name), e("#J-header-logout a.logout").attr("href", logout)) : "Y" === a.data.is_uam_login ? n(a.data) : (isOver = !isOver, o(dynimic_url_path + "/" + a.data.login_url)))
            },
            error: function(e) {
                window.ajaxLogin_flag = !0
            }
        })
    }

    function i() {
        e("#J-header-login").show(), e("#J-header-logout").hide()
    }

    function r() {
        e("#J-header-login").hide(), e("#J-header-logout").show()
    }

    function n(t) {
        e.ajax({
            url: passport_apptk_static,
            data: {
                appid: passport_appId
            },
            xhrFields: {
                withCredentials: !0
            },
            type: "POST",
            timeout: 1e4,
            async: !1,
            success: function(e) {
                "0" == e.result_code ? (isLogin = "Y", window.isLogin = isLogin, window.ajaxLogin = (new Date).getTime(), isOver = !isOver, o(userLogin_url)) : (isLogin = "N", window.isLogin = isLogin, window.ajaxLogin = (new Date).getTime(), isOver = !isOver, o(static_url_path + "/" + t.login_url)), window.ajaxLogin_flag = !0
            },
            error: function(e) {
                window.ajaxLogin_flag = !0
            }
        })
    }

    function o(e) {
        window.location.href = e
    }
    return window.isLogin = "N", window.ajaxLogin_flag = !1, e("#index_ads") && e("#index_ads").length > 0 ? e("#gLink").click(function() {
        return e("html, body").animate({
            scrollTop: e("#index_ads").offset().top
        }, {
            duration: 500,
            easing: "swing"
        }), !1
    }) : e("#gLink").click(function() {
        e("#gLink").attr("href", ggHtml)
    }), {
        initialize: function() {
            t(), window.gHeader = (new Date).getTime()
        }
    }
}), define("g/g-footer", ["jquery"], function(e) {
    function t() {
        var t = e(window).height(),
            a = e(".footer").height(),
            i = e(".content").height(),
            r = t - 109 - a;
        i <= r && e(".content").height(r)
    }
    return {
        initialize: function() {
            t(), window.gFooter = (new Date).getTime()
        }
    }
}), define("g/g-href", ["jquery"], function(e) {
    function t() {
        e('a[name="g_href"]').click(function() {
            var t = e(this).attr("data-redirect"),
                a = e(this).attr("data-type"),
                i = e(this).attr("data-href"),
                r = e(this).attr("data-target");
            "Y" == t ? "_blank" == r ? 1 == a ? window.open(href_baseUrl_1 + href_path_1 + i) : 2 == a ? window.open(href_baseUrl_2 + href_path_2 + i) : 3 == a ? window.open(href_baseUrl_3 + href_path_3 + i) : 4 == a ? window.open(href_baseUrl_4 + href_path_4 + i) : 5 == a ? window.open(href_baseUrl_5 + href_path_5 + i) : 6 == a ? window.open(href_baseUrl_6 + href_path_6 + i) : 10 == a && window.open(href_baseUrl_10 + href_path_10 + i) : 1 == a ? window.location.href = href_baseUrl_1 + href_path_1 + i : 2 == a ? window.location.href = href_baseUrl_2 + href_path_2 + i : 3 == a ? window.location.href = href_baseUrl_3 + href_path_3 + i : 4 == a ? window.location.href = href_baseUrl_4 + href_path_4 + i : 5 == a ? window.location.href = href_baseUrl_5 + href_path_5 + i : 6 == a ? window.location.href = href_baseUrl_6 + href_path_6 + i : 10 == a && (window.location.href = href_baseUrl_10 + href_path_10 + i) : "_blank" == r ? window.open(i) : window.location.href = i
        })
    }
    return {
        initialize: function() {
            t()
        }
    }
}),
    function(e) {
        "function" == typeof define && define.amd ? define("core/common/jquery.SuperSlide", ["jquery"], e) : e(jQuery)
    }(function(e) {
        ! function(e) {
            e.fn.slide = function(t) {
                return e.fn.slide.defaults = {
                    type: "slide",
                    effect: "fade",
                    autoPlay: !1,
                    delayTime: 500,
                    interTime: 2500,
                    triggerTime: 150,
                    defaultIndex: 0,
                    titCell: ".hd li",
                    mainCell: ".bd",
                    targetCell: null,
                    trigger: "mouseover",
                    scroll: 1,
                    vis: 1,
                    titOnClassName: "on",
                    autoPage: !1,
                    prevCell: ".prev",
                    nextCell: ".next",
                    pageStateCell: ".pageState",
                    opp: !1,
                    pnLoop: !0,
                    easing: "swing",
                    startFun: null,
                    endFun: null,
                    switchLoad: null,
                    playStateCell: ".playState",
                    mouseOverStop: !0,
                    defaultPlay: !0,
                    returnDefault: !1
                }, this.each(function() {
                    var a, i = e.extend({}, e.fn.slide.defaults, t),
                        r = e(this),
                        n = i.effect,
                        o = e(i.prevCell, r),
                        s = e(i.nextCell, r),
                        l = e(i.pageStateCell, r),
                        c = e(i.playStateCell, r),
                        u = e(i.titCell, r),
                        d = u.size(),
                        h = e(i.mainCell, r),
                        f = h.children().size(),
                        p = i.switchLoad,
                        v = e(i.targetCell, r),
                        _ = parseInt(i.defaultIndex),
                        m = parseInt(i.delayTime),
                        g = parseInt(i.interTime),
                        y = (parseInt(i.triggerTime), parseInt(i.scroll)),
                        w = "false" != i.autoPlay && 0 != i.autoPlay,
                        b = "false" != i.opp && 0 != i.opp,
                        D = "false" != i.autoPage && 0 != i.autoPage,
                        k = "false" != i.pnLoop && 0 != i.pnLoop,
                        C = "false" != i.mouseOverStop && 0 != i.mouseOverStop,
                        M = "false" != i.defaultPlay && 0 != i.defaultPlay,
                        x = "false" != i.returnDefault && 0 != i.returnDefault,
                        T = isNaN(i.vis) ? 1 : parseInt(i.vis),
                        S = !-[1] && !window.XMLHttpRequest,
                        j = 0,
                        I = 0,
                        O = 0,
                        q = 0,
                        L = i.easing,
                        N = null,
                        F = null,
                        P = null,
                        E = i.titOnClassName,
                        U = u.index(r.find("." + E)),
                        A = _ = -1 == U ? _ : U,
                        H = _,
                        Y = _,
                        Q = f >= T ? f % y != 0 ? f % y : y : 0,
                        B = "leftMarquee" == n || "topMarquee" == n,
                        $ = function() {
                            e.isFunction(i.startFun) && i.startFun(_, d, r, e(i.titCell, r), h, v, o, s)
                        },
                        z = function() {
                            e.isFunction(i.endFun) && i.endFun(_, d, r, e(i.titCell, r), h, v, o, s)
                        },
                        R = function() {
                            u.removeClass(E), M && u.eq(H).addClass(E)
                        };
                    if ("menu" == i.type) return M && u.removeClass(E).eq(_).addClass(E), u.hover(function() {
                        a = e(this).find(i.targetCell);
                        var t = u.index(e(this));
                        F = setTimeout(function() {
                            switch (_ = t, u.removeClass(E).eq(_).addClass(E), $(), n) {
                                case "fade":
                                    a.stop(!0, !0).animate({
                                        opacity: "show"
                                    }, m, L, z);
                                    break;
                                case "slideDown":
                                    a.stop(!0, !0).animate({
                                        height: "show"
                                    }, m, L, z)
                            }
                        }, i.triggerTime)
                    }, function() {
                        switch (clearTimeout(F), n) {
                            case "fade":
                                a.animate({
                                    opacity: "hide"
                                }, m, L);
                                break;
                            case "slideDown":
                                a.animate({
                                    height: "hide"
                                }, m, L)
                        }
                    }), void(x && r.hover(function() {
                        clearTimeout(P)
                    }, function() {
                        P = setTimeout(R, m)
                    }));
                    if (0 == d && (d = f), B && (d = 2), D) {
                        if (f >= T)
                            if ("leftLoop" == n || "topLoop" == n) d = f % y != 0 ? 1 + (f / y ^ 0) : f / y;
                            else {
                                var W = f - T;
                                d = 1 + parseInt(W % y != 0 ? W / y + 1 : W / y), d <= 0 && (d = 1)
                            }
                        else d = 1;
                        u.html("");
                        var J = "";
                        if (1 == i.autoPage || "true" == i.autoPage)
                            for (var Z = 0; Z < d; Z++) J += "<li>" + (Z + 1) + "</li>";
                        else
                            for (var Z = 0; Z < d; Z++) J += i.autoPage.replace("$", Z + 1);
                        u.html(J);
                        var u = u.children()
                    }
                    if (f >= T) {
                        h.children().each(function() {
                            e(this).width() > O && (O = e(this).width(), I = e(this).outerWidth(!0)), e(this).height() > q && (q = e(this).height(), j = e(this).outerHeight(!0))
                        });
                        var G = h.children(),
                            V = function() {
                                for (var e = 0; e < T; e++) G.eq(e).clone().addClass("clone").appendTo(h);
                                for (var e = 0; e < Q; e++) G.eq(f - e - 1).clone().addClass("clone").prependTo(h)
                            };
                        switch (n) {
                            case "fold":
                                h.css({
                                    position: "relative",
                                    width: I,
                                    height: j
                                }).children().css({
                                    position: "absolute",
                                    width: O,
                                    left: 0,
                                    top: 0,
                                    display: "none"
                                });
                                break;
                            case "top":
                                h.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; height:' + T * j + 'px"></div>').css({
                                    top: -_ * y * j,
                                    position: "relative",
                                    padding: "0",
                                    margin: "0"
                                }).children().css({
                                    height: q
                                });
                                break;
                            case "left":
                                h.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; width:' + T * I + 'px"></div>').css({
                                    width: f * I,
                                    left: -_ * y * I,
                                    position: "relative",
                                    overflow: "hidden",
                                    padding: "0",
                                    margin: "0"
                                }).children().css({
                                    float: "left",
                                    width: O
                                });
                                break;
                            case "leftLoop":
                            case "leftMarquee":
                                V(), h.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; width:' + T * I + 'px"></div>').css({
                                    width: (f + T + Q) * I,
                                    position: "relative",
                                    overflow: "hidden",
                                    padding: "0",
                                    margin: "0",
                                    left: -(Q + _ * y) * I
                                }).children().css({
                                    float: "left",
                                    width: O
                                });
                                break;
                            case "topLoop":
                            case "topMarquee":
                                V(), h.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; height:' + T * j + 'px"></div>').css({
                                    height: (f + T + Q) * j,
                                    position: "relative",
                                    padding: "0",
                                    margin: "0",
                                    top: -(Q + _ * y) * j
                                }).children().css({
                                    height: q
                                })
                        }
                    }
                    var K = function(e) {
                            var t = e * y;
                            return e == d ? t = f : -1 == e && f % y != 0 && (t = -f % y), t
                        },
                        X = function(t) {
                            var a = function(a) {
                                for (var i = a; i < T + a; i++) t.eq(i).find("img[" + p + "]").each(function() {
                                    var t = e(this);
                                    if (t.attr("src", t.attr(p)).removeAttr(p), h.find(".clone")[0])
                                        for (var a = h.children(), i = 0; i < a.size(); i++) a.eq(i).find("img[" + p + "]").each(function() {
                                            e(this).attr(p) == t.attr("src") && e(this).attr("src", e(this).attr(p)).removeAttr(p)
                                        })
                                })
                            };
                            switch (n) {
                                case "fade":
                                case "fold":
                                case "top":
                                case "left":
                                case "slideDown":
                                    a(_ * y);
                                    break;
                                case "leftLoop":
                                case "topLoop":
                                    a(Q + K(Y));
                                    break;
                                case "leftMarquee":
                                case "topMarquee":
                                    var i = "leftMarquee" == n ? h.css("left").replace("px", "") : h.css("top").replace("px", ""),
                                        r = "leftMarquee" == n ? I : j,
                                        o = Q;
                                    if (i % r != 0) {
                                        var s = Math.abs(i / r ^ 0);
                                        o = 1 == _ ? Q + s : Q + s - 1
                                    }
                                    a(o)
                            }
                        },
                        ee = function(e) {
                            if (!M || A != _ || e || B) {
                                if (B ? _ >= 1 ? _ = 1 : _ <= 0 && (_ = 0) : (Y = _, _ >= d ? _ = 0 : _ < 0 && (_ = d - 1)), $(), null != p && X(h.children()), v[0] && (a = v.eq(_), null != p && X(v), "slideDown" == n ? (v.not(a).stop(!0, !0).slideUp(m), a.slideDown(m, L, function() {
                                    h[0] || z()
                                })) : (v.not(a).stop(!0, !0).hide(), a.animate({
                                    opacity: "show"
                                }, m, function() {
                                    h[0] || z()
                                }))), f >= T) switch (n) {
                                    case "fade":
                                        h.children().stop(!0, !0).eq(_).animate({
                                            opacity: "show"
                                        }, m, L, function() {
                                            z()
                                        }).siblings().hide();
                                        break;
                                    case "fold":
                                        h.children().stop(!0, !0).eq(_).animate({
                                            opacity: "show"
                                        }, m, L, function() {
                                            z()
                                        }).siblings().animate({
                                            opacity: "hide"
                                        }, m, L);
                                        break;
                                    case "top":
                                        h.stop(!0, !1).animate({
                                            top: -_ * y * j
                                        }, m, L, function() {
                                            z()
                                        });
                                        break;
                                    case "left":
                                        h.stop(!0, !1).animate({
                                            left: -_ * y * I
                                        }, m, L, function() {
                                            z()
                                        });
                                        break;
                                    case "leftLoop":
                                        var t = Y;
                                        h.stop(!0, !0).animate({
                                            left: -(K(Y) + Q) * I
                                        }, m, L, function() {
                                            t <= -1 ? h.css("left", -(Q + (d - 1) * y) * I) : t >= d && h.css("left", -Q * I), z()
                                        });
                                        break;
                                    case "topLoop":
                                        var t = Y;
                                        h.stop(!0, !0).animate({
                                            top: -(K(Y) + Q) * j
                                        }, m, L, function() {
                                            t <= -1 ? h.css("top", -(Q + (d - 1) * y) * j) : t >= d && h.css("top", -Q * j), z()
                                        });
                                        break;
                                    case "leftMarquee":
                                        var i = h.css("left").replace("px", "");
                                        0 == _ ? h.animate({
                                            left: ++i
                                        }, 0, function() {
                                            h.css("left").replace("px", "") >= 0 && h.css("left", -f * I)
                                        }) : h.animate({
                                            left: --i
                                        }, 0, function() {
                                            h.css("left").replace("px", "") <= -(f + Q) * I && h.css("left", -Q * I)
                                        });
                                        break;
                                    case "topMarquee":
                                        var r = h.css("top").replace("px", "");
                                        0 == _ ? h.animate({
                                            top: ++r
                                        }, 0, function() {
                                            h.css("top").replace("px", "") >= 0 && h.css("top", -f * j)
                                        }) : h.animate({
                                            top: --r
                                        }, 0, function() {
                                            h.css("top").replace("px", "") <= -(f + Q) * j && h.css("top", -Q * j)
                                        })
                                }
                                u.removeClass(E).eq(_).addClass(E), A = _, k || (s.removeClass("nextStop"), o.removeClass("prevStop"), 0 == _ && o.addClass("prevStop"), _ == d - 1 && s.addClass("nextStop")), l.html("<span>" + (_ + 1) + "</span>/" + d)
                            }
                        };
                    M && ee(!0), x && r.hover(function() {
                        clearTimeout(P)
                    }, function() {
                        P = setTimeout(function() {
                            _ = H, M ? ee() : "slideDown" == n ? a.slideUp(m, R) : a.animate({
                                opacity: "hide"
                            }, m, R), A = _
                        }, 300)
                    });
                    var te = function(e) {
                            N = setInterval(function() {
                                b ? _-- : _++, ee()
                            }, e || g)
                        },
                        ae = function(e) {
                            N = setInterval(ee, e || g)
                        },
                        ie = function() {
                            C || !w || c.hasClass("pauseState") || (clearInterval(N), te())
                        },
                        re = function() {
                            (k || _ != d - 1) && (_++, ee(), B || ie())
                        },
                        ne = function() {
                            (k || 0 != _) && (_--, ee(), B || ie())
                        },
                        oe = function() {
                            clearInterval(N), B ? ae() : te(), c.removeClass("pauseState")
                        },
                        se = function() {
                            clearInterval(N), c.addClass("pauseState")
                        };
                    if (w ? B ? (b ? _-- : _++, ae(), C && h.hover(se, oe)) : (te(), C && r.hover(se, oe)) : (B && (b ? _-- : _++), c.addClass("pauseState")), c.click(function() {
                        c.hasClass("pauseState") ? oe() : se()
                    }), "mouseover" == i.trigger ? u.hover(function() {
                        var e = u.index(this);
                        F = setTimeout(function() {
                            _ = e, ee(), ie()
                        }, i.triggerTime)
                    }, function() {
                        clearTimeout(F)
                    }) : u.click(function() {
                        _ = u.index(this), ee(), ie()
                    }), B) {
                        if (s.mousedown(re), o.mousedown(ne), k) {
                            var le, ce = function() {
                                    le = setTimeout(function() {
                                        clearInterval(N), ae(g / 10 ^ 0)
                                    }, 150)
                                },
                                ue = function() {
                                    clearTimeout(le), clearInterval(N), ae()
                                };
                            s.mousedown(ce), s.mouseup(ue), o.mousedown(ce), o.mouseup(ue)
                        }
                        "mouseover" == i.trigger && (s.hover(re, function() {}), o.hover(ne, function() {}))
                    } else s.click(re), o.click(ne);
                    if ("auto" == i.vis && 1 == y && ("left" == n || "leftLoop" == n)) {
                        var de, he = function() {
                            S && (h.width("auto"), h.children().width("auto")), h.parent().width("auto"), I = h.parent().width(), S && h.parent().width(I), h.children().width(I), "left" == n ? (h.width(I * f), h.stop(!0, !1).animate({
                                left: -_ * I
                            }, 0)) : (h.width(I * (f + 2)), h.stop(!0, !1).animate({
                                left: -(_ + 1) * I
                            }, 0)), S || I == h.parent().width() || he()
                        };
                        e(window).resize(function() {
                            clearTimeout(de), de = setTimeout(he, 100)
                        }), he()
                    }
                })
            }
        }(jQuery), jQuery.easing.jswing = jQuery.easing.swing, jQuery.extend(jQuery.easing, {
            def: "easeOutQuad",
            swing: function(e, t, a, i, r) {
                return jQuery.easing[jQuery.easing.def](e, t, a, i, r)
            },
            easeInQuad: function(e, t, a, i, r) {
                return i * (t /= r) * t + a
            },
            easeOutQuad: function(e, t, a, i, r) {
                return -i * (t /= r) * (t - 2) + a
            },
            easeInOutQuad: function(e, t, a, i, r) {
                return (t /= r / 2) < 1 ? i / 2 * t * t + a : -i / 2 * (--t * (t - 2) - 1) + a
            },
            easeInCubic: function(e, t, a, i, r) {
                return i * (t /= r) * t * t + a
            },
            easeOutCubic: function(e, t, a, i, r) {
                return i * ((t = t / r - 1) * t * t + 1) + a
            },
            easeInOutCubic: function(e, t, a, i, r) {
                return (t /= r / 2) < 1 ? i / 2 * t * t * t + a : i / 2 * ((t -= 2) * t * t + 2) + a
            },
            easeInQuart: function(e, t, a, i, r) {
                return i * (t /= r) * t * t * t + a
            },
            easeOutQuart: function(e, t, a, i, r) {
                return -i * ((t = t / r - 1) * t * t * t - 1) + a
            },
            easeInOutQuart: function(e, t, a, i, r) {
                return (t /= r / 2) < 1 ? i / 2 * t * t * t * t + a : -i / 2 * ((t -= 2) * t * t * t - 2) + a
            },
            easeInQuint: function(e, t, a, i, r) {
                return i * (t /= r) * t * t * t * t + a
            },
            easeOutQuint: function(e, t, a, i, r) {
                return i * ((t = t / r - 1) * t * t * t * t + 1) + a
            },
            easeInOutQuint: function(e, t, a, i, r) {
                return (t /= r / 2) < 1 ? i / 2 * t * t * t * t * t + a : i / 2 * ((t -= 2) * t * t * t * t + 2) + a
            },
            easeInSine: function(e, t, a, i, r) {
                return -i * Math.cos(t / r * (Math.PI / 2)) + i + a
            },
            easeOutSine: function(e, t, a, i, r) {
                return i * Math.sin(t / r * (Math.PI / 2)) + a
            },
            easeInOutSine: function(e, t, a, i, r) {
                return -i / 2 * (Math.cos(Math.PI * t / r) - 1) + a
            },
            easeInExpo: function(e, t, a, i, r) {
                return 0 == t ? a : i * Math.pow(2, 10 * (t / r - 1)) + a
            },
            easeOutExpo: function(e, t, a, i, r) {
                return t == r ? a + i : i * (1 - Math.pow(2, -10 * t / r)) + a
            },
            easeInOutExpo: function(e, t, a, i, r) {
                return 0 == t ? a : t == r ? a + i : (t /= r / 2) < 1 ? i / 2 * Math.pow(2, 10 * (t - 1)) + a : i / 2 * (2 - Math.pow(2, -10 * --t)) + a
            },
            easeInCirc: function(e, t, a, i, r) {
                return -i * (Math.sqrt(1 - (t /= r) * t) - 1) + a
            },
            easeOutCirc: function(e, t, a, i, r) {
                return i * Math.sqrt(1 - (t = t / r - 1) * t) + a
            },
            easeInOutCirc: function(e, t, a, i, r) {
                return (t /= r / 2) < 1 ? -i / 2 * (Math.sqrt(1 - t * t) - 1) + a : i / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + a
            },
            easeInElastic: function(e, t, a, i, r) {
                var n = 1.70158,
                    o = 0,
                    s = i;
                if (0 == t) return a;
                if (1 == (t /= r)) return a + i;
                if (o || (o = .3 * r), s < Math.abs(i)) {
                    s = i;
                    var n = o / 4
                } else var n = o / (2 * Math.PI) * Math.asin(i / s);
                return -s * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * r - n) * (2 * Math.PI) / o) + a
            },
            easeOutElastic: function(e, t, a, i, r) {
                var n = 1.70158,
                    o = 0,
                    s = i;
                if (0 == t) return a;
                if (1 == (t /= r)) return a + i;
                if (o || (o = .3 * r), s < Math.abs(i)) {
                    s = i;
                    var n = o / 4
                } else var n = o / (2 * Math.PI) * Math.asin(i / s);
                return s * Math.pow(2, -10 * t) * Math.sin((t * r - n) * (2 * Math.PI) / o) + i + a
            },
            easeInOutElastic: function(e, t, a, i, r) {
                var n = 1.70158,
                    o = 0,
                    s = i;
                if (0 == t) return a;
                if (2 == (t /= r / 2)) return a + i;
                if (o || (o = r * (.3 * 1.5)), s < Math.abs(i)) {
                    s = i;
                    var n = o / 4
                } else var n = o / (2 * Math.PI) * Math.asin(i / s);
                return t < 1 ? s * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * r - n) * (2 * Math.PI) / o) * -.5 + a : s * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * r - n) * (2 * Math.PI) / o) * .5 + i + a
            },
            easeInBack: function(e, t, a, i, r, n) {
                return void 0 == n && (n = 1.70158), i * (t /= r) * t * ((n + 1) * t - n) + a
            },
            easeOutBack: function(e, t, a, i, r, n) {
                return void 0 == n && (n = 1.70158), i * ((t = t / r - 1) * t * ((n + 1) * t + n) + 1) + a
            },
            easeInOutBack: function(e, t, a, i, r, n) {
                return void 0 == n && (n = 1.70158), (t /= r / 2) < 1 ? i / 2 * (t * t * ((1 + (n *= 1.525)) * t - n)) + a : i / 2 * ((t -= 2) * t * ((1 + (n *= 1.525)) * t + n) + 2) + a
            },
            easeInBounce: function(e, t, a, i, r) {
                return i - jQuery.easing.easeOutBounce(e, r - t, 0, i, r) + a
            },
            easeOutBounce: function(e, t, a, i, r) {
                return (t /= r) < 1 / 2.75 ? i * (7.5625 * t * t) + a : t < 2 / 2.75 ? i * (7.5625 * (t -= 1.5 / 2.75) * t + .75) + a : t < 2.5 / 2.75 ? i * (7.5625 * (t -= 2.25 / 2.75) * t + .9375) + a : i * (7.5625 * (t -= 2.625 / 2.75) * t + .984375) + a
            },
            easeInOutBounce: function(e, t, a, i, r) {
                return t < r / 2 ? .5 * jQuery.easing.easeInBounce(e, 2 * t, 0, i, r) + a : .5 * jQuery.easing.easeOutBounce(e, 2 * t - r, 0, i, r) + .5 * i + a
            }
        })
    });
var formatDate = function(e) {
    var t = e.getFullYear(),
        a = e.getMonth() + 1;
    a = a < 10 ? "0" + a : a;
    var i = e.getDate();
    return i = i < 10 ? "0" + i : i, t + "-" + a + "-" + i
};
Array.prototype.distinct = function() {
    var e, t, a = this,
        i = a.length;
    for (e = 0; e < i; e++)
        for (t = e + 1; t < i; t++) a[e] == a[t] && (a.splice(t, 1), i--, t--);
    return a
};
var formatDateNextMonth = function(e) {
        var t = new Date,
            a = new Date(t);
        return a.setDate(t.getDate() + 29), a.getFullYear() + "-" + (a.getMonth() + 1) + "-" + a.getDate()
    },
    userinfo_messages = {
        "userinfo_message.confirm_info": "您确认吗？",
        "userinfo_message.title_info": "信息提示",
        "userinfo_message.error_info": "错误提示",
        "userinfo_button.ok_info": "确认",
        "userinfo_phone.null_error": "请输入手机号！",
        "userinfo_phone.ismobile_error": "您输入的手机号码不是有效的格式！",
        "userinfo_email.null_error": "请输入电子邮件地址！",
        "userinfo_email.isemail_error": "请输入有效的电子邮件地址！",
        "userinfo_email.remote_error": "电子邮件地址已被注册，请使用其他email！",
        "userinfo_name.null_error": "请输入您的姓名！",
        "userinfo_name.range_length_error": "允许输入的字符串在3-30个字符之间！",
        "userinfo_name.:char_blank_error": "姓名只能包含中文或者英文，如有生僻字或繁体字参见姓名填写规则进行填写！",
        "userinfo_sex.:null_error": "请选择性别！",
        "userinfo_password.null_error": "请输入密码！",
        "userinfo_password.length_error": "密码长度不能少于6个字符！",
        "userinfo_telephone.pattern_error": "固定电话格式错误",
        "userinfo_idtype.null_error": "请选择证件类型！",
        "userinfo_idno.null_error": "请输入证件号码！",
        "userinfo_idno.id_valid_error": "输入的证件号码中包含中文信息或特殊字符！",
        "userinfo_idno.sec_id_card_error": "请正确输入18位的证件号码！",
        "userinfo_idno.fir_id_card_error": "请正确输入15或者18位的证件号码！",
        "userinfo_idno.check_Hkmacao_error": "请输入有效的港澳居民通行证号码！",
        "userinfo_idno.check_taiw_error": "请输入有效的台湾居民通行证号码！",
        "userinfo_idno.check_passport_error": "请输入有效的护照号码！",
        "userinfo_idno.check_work_error": "请输入有效的外国人居留证号码！",
        "userinfo_student.province_name_error": "请输入省份！",
        "userinfo_student.school_name_error": "请输入学校！",
        "userinfo_student.student_no_error": "请输入学号！",
        "userinfo_student.school_system_error": "请选择学制！",
        "userinfo_student.enter_year_error": "请选择入学年份！"
    };
define("core/common/mUtils", function() {}),
    function(e) {
        "function" == typeof define && define.amd ? define("core/common/common", ["jquery"], e) : e(jQuery)
    }(function(e) {
        var t = function() {
                e(".js-gotop").on("click", function(t) {
                    return t.preventDefault(), e("html, body").animate({
                        scrollTop: e("html").offset().top
                    }, 500, "easeInOutExpo"), !1
                }), e(window).scroll(function() {
                    e(window).scrollTop() > 200 ? e(".js-top").addClass("active") : e(".js-top").removeClass("active")
                })
            },
            a = function() {
                var t = e(window).height() - e(".header").outerHeight(!0) - e(".footer").outerHeight(!0);
                e(".content").css("min-height", t)
            };
        e(function() {
            t(), a()
        }), e(window).on("resize", function() {
            a()
        });
        e(".center-menu .icon-switch").on("click", function() {
            var t = e(this),
                a = t.parent().next();
            if (a.is(".menu-sub") && a.is(":visible")) a.slideUp(300, function() {
                a.parent("li").addClass("menu-less")
            });
            else if (a.is(".menu-sub") && !a.is(":visible")) {
                var i = t.parents(".menu-item");
                a.slideDown(300, function() {
                    i.removeClass("menu-less")
                })
            }
        }), e("body").on("click", ".order-panel .icon-fold", function() {
            var t = e(this),
                a = t.parent().next();
            if (a.is(".order-item-bd") && a.is(":visible")) a.slideUp(300, function() {
                a.parents(".order-item").addClass("show-less")
            });
            else if (a.is(".order-item-bd") && !a.is(":visible")) {
                var i = t.parents(".order-item");
                a.slideDown(300, function() {
                    i.removeClass("show-less")
                })
            }
        }), e("#js-minHeight").css("minHeight", e(".center-menu").outerHeight()), e(".center-main .tab-item").css("minHeight", e(".center-menu").outerHeight() - 42), e("body").on("click", function(t) {
            e(".sel").removeClass("active")
        }), e("body").on("click", ".sel .sel-hd", function(t) {
            t.stopPropagation(), e(".sel").removeClass("active"), e(this).parent().addClass("active")
        }), e("body").on("click", '.sel .sel-list li:not(".disabled")', function(t) {
            t.stopPropagation();
            var a = e(this).html(),
                i = e(this).parents(".sel");
            e(this).addClass("selected").siblings().removeClass("selected"), i.find(".sel-inner").html(a), i.removeClass("active")
        })
    }),
    function(e) {
        "function" == typeof define && define.amd ? define("core/common/data.jcokies", ["jquery"], e) : e(jQuery)
    }(function(e) {
        jQuery.extend({
            jc_getFromStation: function() {
                return e.jc_getcookie("_jc_save_fromStation")
            },
            jc_setFromStation: function(t, a) {
                if (void 0 === t || void 0 === a || "" == t || "" == a) throw "参数错误";
                var i = t + "," + a;
                e.jc_setcookie("_jc_save_fromStation", i, 10)
            },
            jc_getToStation: function() {
                return e.jc_getcookie("_jc_save_toStation")
            },
            jc_setToStation: function(t, a) {
                if (void 0 === t || void 0 === a || "" == t || "" == a) throw "参数错误";
                var i = t + "," + a;
                e.jc_setcookie("_jc_save_toStation", i, 10)
            },
            jc_getFromDate: function() {
                return e.jc_getcookie("_jc_save_fromDate")
            },
            jc_setFromDate: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_fromDate", a, 10)
            },
            jc_getTrainNumber: function() {
                return e.jc_getcookie("_jc_save_trainNumber")
            },
            jc_setTrainNumber: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_trainNumber", a, 10)
            },
            jc_zGetTrainStition: function() {
                return e.jc_getcookie("_jc_save_zwdch_fromStation")
            },
            jc_zSetTrainStition: function(t, a) {
                if (void 0 === t || void 0 === a || "" == t || "" == a) throw "参数错误";
                var i = t + "," + a;
                e.jc_setcookie("_jc_save_zwdch_fromStation", i, 10)
            },
            jc_zGetTrainNumber: function() {
                return e.jc_getcookie("_jc_save_zwdch_cc")
            },
            jc_zSetTrainNumber: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_zwdch_cc", a, 10)
            },
            jc_getIsStudent: function() {
                return e.jc_getcookie("_jc_save_stuFlag_flag")
            },
            jc_setIsStudent: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_stuFlag_flag", a, 10)
            },
            jc_setIsGD: function() {
                return e.jc_getcookie("_jc_save_gdFlag_flag")
            },
            jc_setIsGD: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_gdFlag_flag", a, 10)
            },
            jc_setPageFrom: function() {
                return e.jc_getcookie("jc_setPageFrom")
            },
            jc_setPageFrom: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("jc_setPageFrom", a, 10)
            },
            jc_saveZzwdch: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_zwdch_cxlx", a, 10)
            },
            jc_getToDate: function() {
                return e.jc_getcookie("_jc_save_toDate")
            },
            jc_setToDate: function(t) {
                void 0 === t && (t = "");
                var a = t;
                e.jc_setcookie("_jc_save_toDate", a, 10)
            },
            jc_getWfOrDc: function() {
                return e.jc_getcookie("_jc_save_wfdc_flag")
            },
            jc_setWfOrDc: function(t) {
                if (void 0 === t) throw "参数错误";
                var a = t;
                e.jc_setcookie("_jc_save_wfdc_flag", a, 10)
            },
            jc_getcookie: function(e) {
                var t = document.cookie.indexOf(e),
                    a = document.cookie.indexOf(";", t);
                return -1 == t ? "" : unescape(document.cookie.substring(t + e.length + 1, a > t ? a : document.cookie.length))
            },
            jc_setcookie: function(e, t, a, i, r, n) {
                var o = document.domain;
                o = o.substring(o.indexOf(".") + 1, o.length);
                var s = new Date;
                s.setTime(s.getTime() + 1e3 * a), document.cookie = escape(e) + "=" + escape(t) + (i ? "; path=" + i : ";path=/") + "; domain=" + o + (n ? "; secure" : "") + ";expires=" + s
            }
        })
    });
var static_url = "https://kyfw.12306.cn",
    search_base_url = "https://search.12306.cn",
    send_url = "https://tj.12306.cn",
    qr_code = "https://kyfw.12306.cn",
    publicName = "/otn",
    path = "/otn",
    static_url_path = static_url + path,
    getSearchUrl = search_base_url + "/search/v1/h5/search",
    getNotTripUrl = static_url_path + "/queryOrder/queryMyOrder",
    getNotCompleteUrl = static_url_path + "/queryOrder/queryMyOrderNoComplete",
    iscantuipiao = static_url_path + "/queryOrder/returnTicketAffirm",
    order_url = static_url_path + "/psr/query",
    qxyyUrl = static_url_path + "/icentre/qxyyApi",
    stopTrainUrl = static_url_path + "/icentre/stopTrain",
    isRepeatSubmitUrl = static_url_path + "/icentre/isRepeatSubmit",
    addQxyyUrl = static_url_path + "/icentre/addQxyy",
    lostItemsApiUrl = static_url_path + "/icentre/lostItemsApi",
    addLostItemsUrl = static_url_path + "/icentre/addLostItems",
    lostItemsUrl = static_url_path + "/icentre/lostItems",
    serviceQueryHtml = static_url_path + "/view/icentre_serviceQuery.html",
    orderInit = static_url_path + "/orderdetail/initApi",
    queryOrderDetail = static_url_path + "/orderdetail/queryOrderDetail",
    checkBeforeReturnIsu = static_url_path + "/orderdetail/checkBeforeReturnIsu",
    queryRefundInfo = static_url_path + "/orderdetail/queryRefundInfo",
    returnIsu = static_url_path + "/orderdetail/returnIsu",
    payfinishApi = static_url_path + "/orderdetail/payfinishApi",
    paycheckNew = static_url_path + "/orderdetail/paycheckNew",
    queryMyIns = static_url_path + "/insurance/queryMyIns",
    queryOrderForQii = static_url_path + "/orderdetail/queryOrderForQii",
    initQueryUserInfoApi = static_url_path + "/modifyUser/initQueryUserInfoApi",
    saveModifyUserInfo = static_url_path + "/modifyUser/saveModifyUserInfo",
    cancelDeliveryUrl = static_url_path + "/queryOrder/cancelDelivery",
    queryDeliverInfo = static_url_path + "/orderdetail/queryDeliverInfo",
    initServiceQuery = static_url_path + "/icentre/initServiceQuery",
    trackDeliveryDetailUrl = static_url_path + "/queryOrder/trackDeliveryDetail",
    stopTrain = static_url_path + "/icentre/stopTrain",
    adviceApi = static_url_path + "/advice/adviceApi",
    addAdvice = static_url_path + "/advice/addAdvice",
    complaintApi = static_url_path + "/advice/complaintApi",
    initcomplaintService = static_url_path + "/advice/initcomplaintService",
    addServiceQuality = static_url_path + "/advice/addServiceQuality",
    initcomplaintNet = static_url_path + "/advice/initcomplaintNet",
    addNetSale = static_url_path + "/advice/addNetSale",
    uploading = static_url_path + "/advice/uploading",
    userSecurityInitApi = static_url_path + "/userSecurity/initApi",
    userSecurityDoEditSafeEmail = static_url_path + "/userSecurity/doEditSafeEmail",
    userSecurityCheckUserIsActive = static_url_path + "/userSecurity/checkUserIsActive",
    userSecuritySafeEmailApi = static_url_path + "/userSecurity/safeEmailApi",
    addressInitApi = static_url_path + "/address/initApi",
    addressAddInitApi = static_url_path + "/address/addInitApi",
    addressEditApi = static_url_path + "/address/edit",
    addressDeleteApi = static_url_path + "/address/delete",
    addressAddApi = static_url_path + "/address/add",
    addressGetProvince = static_url_path + "/address/getProvince",
    addressGetCity = static_url_path + "/address/getCity",
    addressGetCountry = static_url_path + "/address/getCountry",
    addressGetTown = static_url_path + "/address/getTown",
    addressGetStreet = static_url_path + "/address/getStreet",
    userLogin_url = static_url_path + "/login/userLogin",
    ticket_notice_url = static_url_path + "/psr/getItineraryNotice",
    personal_welcome_url = static_url_path + "/index/initMy12306Api",
    refundUrl = static_url_path + "/queryOrder/returnTicketAffirm",
    returnUrl = static_url_path + "/queryOrder/returnTicket",
    turnUrl = static_url_path + "/queryOrder/returnTicketRedirect",
    resginUrl = static_url_path + "/queryOrder/resginTicket",
    cateringUrl = static_url_path + "/queryOrder/queryCateringParams",
    noticeUrl = static_url_path + "/view/userSecurity_accountBindInfo.html",
    downUrl = static_url_path + "/psr/downloadItineraryNotice",
    loginConf = static_url_path + "/login/conf",
    passport_appId = "otn",
    passport_apptk_static = static_url + "/passport/web/auth/uamtk",
    qr_codeurl = static_url + "/passport/web/create-verifyqr64",
    success_qrcode_url = static_url_path + "/psr/checkVerifyqr",
    late_url = static_url_path + "/zwdch/init",
    logout = static_url_path + "/login/loginOut",
    getMobileCode4pwdemail = static_url_path + "/userSecurity/getMobileCode4pwdemail",
    safeEmail = static_url_path + "/userSecurity/safeEmailApi",
    passwordChange = static_url_path + "/userSecurity/loginPwdApi",
    confirmChangePassword = static_url_path + "/userSecurity/editLoginPwd",
    userSecurityInit = static_url_path + "userSecurity/init",
    noticeSetting = static_url_path + "/userSecurity/accountBindInfoApi",
    updateSendMsgType = static_url_path + "/userSecurity/updateSendMsgType",
    accountUnbind = static_url_path + "/userSecurity/accountUnbind",
    requestAliQr = static_url_path + "/index/requestAliQr",
    requestWechatQr = static_url_path + "/index/requestWechatQr",
    accountBindInfo = static_url_path + "/userSecurity/accountBindInfo",
    userSecurity = static_url_path + "/view/userSecurity.html",
    turnToIndex = "https://www.12306.cn/index/index.html",
    checkMobileCode = static_url_path + "/userSecurity/checkMobileCode",
    initQueryUserInfo = static_url_path + "/view/information.html",
    doEditTel = static_url_path + "/userSecurity/doEditTel",
    checkMobileCode = static_url_path + "/userSecurity/checkMobileCode",
    bindTelApi = static_url_path + "/userSecurity/bindTelApi",
    stu_control = 60,
    other_control = 30,
    isOver = !0,
    htmlHref = {
        orderDetail: static_url_path + "/view/order.html#DETAIL",
        orderRefund: static_url_path + "/view/order.html#REFUND",
        orderDeliver: static_url_path + "/view/order.html#DELIVER",
        orderInsurance: static_url_path + "/view/order.html#INSURANCE",
        advice: static_url_path + "/view/advice_advice.html",
        complaint: static_url_path + "/view/advice_complaint.html",
        addressInit: static_url_path + "/view/address_init.html",
        userSecurity: static_url_path + "/view/userSecurity.html",
        userSecurityAccountBindInfo: static_url_path + "/view/userSecurity_accountBindInfo.html",
        passengers: static_url_path + "/view/passengers.html",
        index: static_url_path + "/view/index.html",
        browserForie: href_baseUrl_1 + href_path_1 + "view/forie.html"
    },
    href_baseUrl_1 = "https://www.12306.cn/",
    href_path_1 = "index/",
    href_baseUrl_2 = "https://kyfw.12306.cn/",
    href_path_2 = "otn/",
    href_baseUrl_3 = "https://cx.12306.cn/",
    href_path_3 = "tlcx/",
    href_baseUrl_4 = "https://www.12306.cn/",
    href_path_4 = "mormhweb/",
    href_baseUrl_5 = "https://travel.12306.cn/",
    href_path_5 = "portal/",
    href_baseUrl_6 = "https://dynamic.12306.cn/",
    href_path_6 = "otn/",
    href_baseUrl_10 = "https://exservice.12306.cn/",
    href_path_10 = "excater/",
    ggHtml = href_baseUrl_1 + href_path_1 + "index.html#index_ads",
    oneTopContactsUrl = static_url_path + "/passengers/showApi",
    topContactsUrl = static_url_path + "/passengers/query",
    deleteContactUrl = static_url_path + "/passengers/delete",
    editContactUrl = static_url_path + "/passengers/edit",
    addContactUrl = static_url_path + "/passengers/add",
    getSchoolUrl = static_url_path + "/userCommon/schoolNames",
    getCityUrl = static_url_path + "/userCommon/allCitys",
    toPassengers = static_url_path + "/view/passengers.html",
    toPassengerEdit = static_url_path + "/view/passenger_edit.html?type=edit",
    toPassengerAdd = static_url_path + "/view/passenger_edit.html?type=add",
    getQueryRefundInfo = static_url_path + "/queryRefund/queryRefundInfo",
    toleftTicketInit = static_url_path + "/leftTicket/init",
    getinitNoCompleteQueueApi = static_url_path + "/queryOrder/initNoCompleteQueueApi",
    getcancelNoCompleteMyOrder = static_url_path + "/queryOrder/cancelNoCompleteMyOrder",
    getcontinuePayNoCompleteMyOrder = static_url_path + "/queryOrder/continuePayNoCompleteMyOrder",
    getpayOrderInit = static_url_path + "/payOrder/init",
    getconfirmPassengerReport = static_url_path + "/confirmPassenger/report",
    getinitNoComplete = static_url_path + "/view/train_order.html",
    getcancelQueueNoCompleteMyOrder = static_url_path + "/queryOrder/cancelQueueNoCompleteMyOrder";
define("core/common/url_config", function() {}),
    function(e) {
        "function" == typeof define && define.amd ? define("core/common/data.jcalendar", ["jquery"], e) : e(jQuery)
    }(function(e) {
        var t = !0,
            a = e('<div class="cal-wrap" style="z-index:30000;display:none;position: absolute;left: 23px;top: 23px; "><div class="cal"><div class="cal-top"><a href="javascript:void(0);" class="first"></a><a href="javascript:void(0);" class="prev"></a><div class="month"><input type="text" value="" readonly="readonly" disabled="disabled"/><ul class="time-list"><li>一月</li><li>二月</li><li>三月</li><li>四月</li><li>五月</li><li>六月</li><li>七月</li><li>八月</li><li>九月</li><li>十月</li><li>十一月</li><li>十二月</li></ul></div><div class="year"><input type="text" value="" readonly="readonly" disabled="disabled"/><div class="time-list"><ul class="clearfix"><li>2016</li></ul><div class="time-list-ft"><a href="javascript:void(0);" class="fl">←</a><a href="javascript:void(0);" class="fr">→</a><a href="javascript:void(0);" class="close">×</a></div></div></div><a href="javascript:void(0);" class="last"></a><a href="javascript:void(0);" class="next"></a></div><ul class="cal-week"><li><b>日</b></li><li>一</li><li>二</li><li>三</li><li>四</li><li>五</li><li><b>六</b></li></ul><div class="cal-cm"></div></div><div class="cal cal-right"><div class="cal-top"><a href="javascript:void(0);" class="last"></a><a href="javascript:void(0);" class="next"></a><div class="year"><input type="text" value="" readonly="readonly" disabled="disabled"/><div class="time-list"><ul class="clearfix"><li>2016</li></ul><div class="time-list-ft"><a href="javascript:void(0);" class="fl">←</a><a href="javascript:void(0);" class="fr">→</a><a href="javascript:void(0);" class="close">×</a></div></div></div><div class="month"><input type="text" value="" readonly="readonly" disabled="disabled"/><ul class="time-list"><li>一月</li><li>二月</li><li>三月</li><li>四月</li><li>五月</li><li>六月</li><li>七月</li><li>八月</li><li>九月</li><li>十月</li><li>十一月</li><li>十二月</li></ul></div></div><ul class="cal-week"><li><b>日</b></li><li>一</li><li>二</li><li>三</li><li>四</li><li>五</li><li><b>六</b></li></ul><div class="cal-cm"></div></div><div class="cal-ft"><a href="javascript:void(0);" class="cal-btn">今天</a></div></div>'),
            i = e(a);
        e(document.body).append(i);
        var r = i.find("div"),
            n = i.find("a"),
            o = i.find("input"),
            s = i.find("ul");
        e.jcalendar = function(a, l) {
            function c(e) {
                return document.createElement(e)
            }

            function u(e, t, a, i) {
                var r = new y(new Date(e, t, 1)),
                    n = new y(new Date(a, i, 1));
                K.init(r, 0), X.draw(1), K.init(n, 1), X.draw(0), X.resetYM(r, n)
            }

            function d(e) {
                return e = x ? e.replace(x, "") : e, e = T ? e.replace(T, "") : e
            }

            function h() {
                A[0] && e(A[1]).attr("class") == A[2] && p(i, A[3], !1), f() ? i[0].children[2].children[0].style.color = q : i[0].children[2].children[0].style.color = "#297405", v(i, e(b).val())
            }

            function f() {
                var e = new Date(S),
                    t = new Date(j),
                    a = new Date,
                    i = new Date(a.getFullYear(), a.getMonth(), a.getDate());
                return i > t || i < e
            }

            function p(e, t, a) {
                t = d(t);
                var i = e[0].children[0].children[0].children[3].children[0].value,
                    r = _(e[0].children[0].children[0].children[2].children[0].value),
                    n = e[0].children[0].children[2].children,
                    o = e[0].children[1].children[2].children;
                for (var s in n)
                    if (n[s].children) {
                        var l = n[s].children[0].numHTML,
                            c = new Date(i, r - 1, l),
                            u = new Date(t.substring(0, 4), t.substring(5, 7) - 1, t.substring(8, 10)),
                            h = a ? c < u : c > u;
                        h && (n[s].children[0].style.color = q, "2" == E && (n[s].children[1].style.color = q), n[s].onclick = null, n[s].style.cursor = "auto")
                    }
                for (var s in o)
                    if (o[s].children) {
                        var l = o[s].children[0].numHTML,
                            c = new Date(i, r, l),
                            u = new Date(t.substring(0, 4), t.substring(5, 7) - 1, t.substring(8, 10)),
                            h = a ? c < u : c > u;
                        h && (o[s].children[0].style.color = q, "2" == E && (n[s].children[1].style.color = q), o[s].onclick = null, o[s].style.cursor = "auto")
                    }
            }

            function v(e, t) {
                if ((t = d(t)) && t.length >= 10) {
                    t = t.substring(0, 10);
                    var a = e[0].children[0].children[0].children[3].children[0].value,
                        i = _(e[0].children[0].children[0].children[2].children[0].value),
                        r = e[0].children[0].children[2].children,
                        n = e[0].children[1].children[2].children;
                    for (var o in r)
                        if (r[o].children) {
                            var s = r[o].children[0].numHTML,
                                l = new Date(a, i - 1, s),
                                c = new Date(t.substring(0, 4), t.substring(5, 7) - 1, t.substring(8, 10));
                            l.getTime() == c.getTime() ? (r[o].style.border = "1px solid #a5b9da", r[o].style.background = O) : (r[o].style.border = "", r[o].style.background = "")
                        }
                    for (var o in n)
                        if (n[o].children) {
                            var s = n[o].children[0].numHTML,
                                l = new Date(a, i, s),
                                c = new Date(t.substring(0, 4), t.substring(5, 7) - 1, t.substring(8, 10));
                            l.getTime() == c.getTime() ? (n[o].style.border = "1px solid #a5b9da", n[o].style.background = O) : (n[o].style.border = "", n[o].style.background = "")
                        }
                }
            }

            function _(e) {
                return "一月" == e ? 1 : "二月" == e ? 2 : "三月" == e ? 3 : "四月" == e ? 4 : "五月" == e ? 5 : "六月" == e ? 6 : "七月" == e ? 7 : "八月" == e ? 8 : "九月" == e ? 9 : "十月" == e ? 10 : "十一月" == e ? 11 : "十二月" == e ? 12 : e
            }

            function m(e) {
                var t = s[e].children;
                for (var a in t)
                    if (t[a].innerHTML) {
                        var i = 0 == e ? o[1].value : o[2].value,
                            r = _(t[a].innerHTML),
                            n = S.substring(0, 4),
                            l = Number(S.substring(5, 7)),
                            c = j.substring(0, 4),
                            u = Number(j.substring(5, 7));
                        i < n || i > c || i == n && r < l || i == c && r > u ? (t[a].style.color = q, t[a].style.cursor = "auto") : (t[a].style.color = F, t[a].style.cursor = "pointer")
                    }
            }

            function g(e, t) {
                s[e].innerHTML = "";
                for (var a = "", i = t - 5; i <= t + 4; i++) i < S.substring(0, 4) || i > j.substring(0, 4) ? a += '<li style="color: ' + q + ';cursor:auto;">' + i + "</li>" : a += '<li style="color: ' + F + ';cursor:pointer;">' + i + "</li>";
                s[e].innerHTML = a;
                var n = 1 == e ? r[5] : r[11];
                if (Number(s[e].children[0].innerHTML) - 1 < S.substring(0, 4) ? (n.children[0].style.color = q, n.children[0].style.cursor = "auto") : (n.children[0].style.color = F, n.children[0].style.cursor = "pointer"), Number(s[e].children[9].innerHTML) + 1 > j.substring(0, 4) ? (n.children[1].style.color = q, n.children[1].style.cursor = "auto") : (n.children[1].style.color = F, n.children[1].style.cursor = "pointer"), 3 == e) var l = s[3].parentElement.getElementsByTagName("li");
                else if (1 == e) var l = r[4].getElementsByTagName("li");
                for (var c = 0; c < l.length; c++) l[c].innerHTML < S.substring(0, 4) || l[c].innerHTML > j.substring(0, 4) ? l[c].onclick = function() {
                    r[4].style.display = "none", r[10].style.display = "none"
                } : l[c].onclick = function() {
                    var t = this.innerHTML,
                        a = 3 == e ? _(o[3].value) + "" : _(o[0].value) + "";
                    a = 1 == a.length ? "0" + a : a, 3 == e ? (u(t, a - 2, t, a - 1), s[3].parentElement.style.display = "none") : 1 == e && (u(t, a - 1, t, a), r[4].style.display = "none"), h()
                }
            }

            function y(e) {
                function t(e, t) {
                    return new Date(31556925974.7 * (e - 1900) + 6e4 * R[t] + Date.UTC(1900, 0, 6, 2, 5)).getUTCDate()
                }

                function a(e) {
                    var t, a = 348;
                    for (t = 32768; t > 8; t >>= 1) a += $[e - 1900] & t ? 1 : 0;
                    return a + i(e)
                }

                function i(e) {
                    return r(e) ? 65536 & $[e - 1900] ? 30 : 29 : 0
                }

                function r(e) {
                    return 15 & $[e - 1900]
                }

                function n(e, t) {
                    return $[e - 1900] & 65536 >> t ? 30 : 29
                }

                function o(e) {
                    var t, o = 0,
                        s = 0,
                        l = new Date(1900, 0, 31),
                        c = (e - l) / 864e5;
                    for (this.dayCyl = c + 40, this.monCyl = 14, t = 1900; t < 2050 && c > 0; t++) s = a(t), c -= s, this.monCyl += 12;
                    for (c < 0 && (c += s, t--, this.monCyl -= 12), this.year = t, this.yearCyl = t - 1864, o = r(t), this.isLeap = !1, t = 1; t < 13 && c > 0; t++) o > 0 && t == o + 1 && 0 == this.isLeap ? (--t, this.isLeap = !0, s = i(this.year)) : s = n(this.year, t), 1 == this.isLeap && t == o + 1 && (this.isLeap = !1), c -= s, 0 == this.isLeap && this.monCyl++;
                    0 == c && o > 0 && t == o + 1 && (this.isLeap ? this.isLeap = !1 : (this.isLeap = !0, --t, --this.monCyl)), c < 0 && (c += s, --t, --this.monCyl), this.month = t, this.day = c + 1
                }

                function s(e) {
                    return e < 10 ? "0" + e : e
                }

                function l(e, t) {
                    var a = e;
                    return t.replace(/dd?d?d?|MM?M?M?|yy?y?y?/g, function(e) {
                        switch (e) {
                            case "yyyy":
                                var t = "000" + a.getFullYear();
                                return t.substring(t.length - 4);
                            case "dd":
                                return s(a.getDate());
                            case "d":
                                return 1 == a.getDate().toString().length ? "0" + a.getDate().toString() : a.getDate().toString();
                            case "MM":
                                return s(a.getMonth() + 1);
                            case "M":
                                return 1 == (a.getMonth() + 1).toString().length ? "0" + (a.getMonth() + 1).toString() : (a.getMonth() + 1).toString()
                        }
                    })
                }
                this.date = e, this.isToday = !1, this.isRestDay = !1, this.solarYear = l(e, "yyyy"), this.solarMonth = l(e, "MM"), this.solarDate = l(e, "dd"), this.calendarDate = new Date(this.solarYear, this.solarMonth - 1, this.solarDate), this.solarWeekDay = e.getDay(), this.solarWeekDayInChinese = "星期" + W.charAt(this.solarWeekDay);
                var c = new o(e);
                this.lunarYear = c.year, this.lunarMonth = c.month, this.lunarIsLeapMonth = c.isLeap, this.lunarMonthInChinese = this.lunarIsLeapMonth ? "闰" + J[c.month - 1] : J[c.month - 1], this.lunarDate = c.day, this.showInLunar = this.lunarDateInChinese = function(e, t) {
                    var a;
                    switch (t) {
                        case 10:
                            a = "初十";
                            break;
                        case 20:
                            a = "二十";
                            break;
                        case 30:
                            a = "三十";
                            break;
                        default:
                            a = Z.charAt(Math.floor(t / 10)), a += W.charAt(t)
                    }
                    return a
                }(this.lunarMonth, this.lunarDate), 1 == this.lunarDate && (this.showInLunar = this.lunarMonthInChinese + "月"), this.jieqi = "", this.restDays = 0, t(this.solarYear, 2 * (this.solarMonth - 1)) == l(e, "d") && (this.showInLunar = this.jieqi = z[2 * (this.solarMonth - 1)]), t(this.solarYear, 2 * (this.solarMonth - 1) + 1) == l(e, "d") && (this.showInLunar = this.jieqi = z[2 * (this.solarMonth - 1) + 1]), "清明" == this.showInLunar && (this.showInLunar = "清明", this.restDays = 1), this.solarFestival = G[l(e, "MM") + l(e, "dd")], void 0 === this.solarFestival ? this.solarFestival = "" : /\*(\d)/.test(this.solarFestival) && (this.restDays = parseInt(RegExp.$1), this.solarFestival = this.solarFestival.replace(/\*\d/, "")), this.showInLunar = "" == this.solarFestival ? this.showInLunar : this.solarFestival, this.lunarFestival = V[this.lunarIsLeapMonth ? "00" : s(this.lunarMonth) + s(this.lunarDate)], void 0 === this.lunarFestival ? this.lunarFestival = "" : /\*(\d)/.test(this.lunarFestival) && (this.restDays = this.restDays > parseInt(RegExp.$1) ? this.restDays : parseInt(RegExp.$1), this.lunarFestival = this.lunarFestival.replace(/\*\d/, "")), 12 == this.lunarMonth && this.lunarDate == n(this.lunarYear, 12) && (this.lunarFestival = V["0100"], this.restDays = 1), this.showInLunar = "" == this.lunarFestival ? this.showInLunar : this.lunarFestival, this.showInLunar = this.showInLunar.length > 4 ? this.showInLunar.substr(0, 2) + "..." : this.showInLunar, "清明" == this.showInLunar && (this.solarFestival = "清明")
            }
            var w = this;
            w.$el = e(a), w.el = a, w.options = e.extend({}, e.jcalendar.defaultOptions, l);
            var b = w.el.selector,
                D = {
                    closeView: w.options.closeCalendar
                },
                k = w.options.onpicked;
            e(b)[0].onchange = k;
            var C = w.options.isSingle,
                M = w.options.showFormat,
                x = w.options.formatBeforeInfo,
                T = w.options.formatAfterInfo,
                S = w.options.startDate;
            S = S || "1901-01-01";
            var j = w.options.endDate;
            j = j || "2050-12-31", S = S.substring(0, 4) + "/" + S.substring(5, 7) + "/" + S.substring(8, 10), j = j.substring(0, 4) + "/" + j.substring(5, 7) + "/" + j.substring(8, 10);
            var I = w.options.isTodayBlock,
                O = w.options.todayClickColor,
                q = w.options.noClickColor,
                L = w.options.restColor,
                N = w.options.noRestColor,
                F = w.options.clickByYearMonth,
                P = w.options.lunarColor,
                E = w.options.isTwoRows,
                U = w.options.isYearMonthDisabled,
                A = w.options.condition,
                H = {
                    "2018-09-29": "班",
                    "2018-09-30": "班"
                },
                Y = {
                    "2018-09-22": "休",
                    "2018-09-23": "休",
                    "2018-09-24": "休",
                    "2018-10-01": "休",
                    "2018-10-02": "休",
                    "2018-10-03": "休",
                    "2018-10-04": "休",
                    "2018-10-05": "休",
                    "2018-10-06": "休",
                    "2018-10-07": "休"
                };
            document.onclick = function(e) {
                t || (i.hide(), D.closeView())
            }, e(b).mouseout(function() {
                t = !1
            }), e(b).mouseover(function() {
                t = !0
            }), i.mouseover(function() {
                e(b).unbind("blur")
            }), i.click(function(e) {
                e.stopPropagation(), t = !1
            }), i.mouseout(function() {
                e(b).bind("blur", function() {
                    i.hide()
                })
            }), r[14].onclick = function() {
                if (!f()) {
                    var t = new Date,
                        a = t.getFullYear(),
                        r = t.getMonth() + 1;
                    r >= 1 && r <= 9 && (r = "0" + r);
                    var n = t.getDate();
                    n >= 0 && n <= 9 && (n = "0" + n);
                    var o = a + "-" + r + "-" + n,
                        s = M ? o : o + " " + B[t.getDay()];
                    s = x ? x + s : s, s = T ? s + T : s, e(b).val(s), e(b).change(), i.hide()
                }
            }, n[0].onclick = function() {
                var e = o[1].value,
                    t = _(o[0].value),
                    a = new Date(e - 1, t, 1),
                    i = new Date(a.getTime() - 864e5);
                if (new Date(i.getFullYear(), Number(i.getMonth()), i.getDate()) >= new Date(S)) u(e - 1, t - 1, e - 1, t);
                else {
                    var a = new Date(S);
                    u(a.getFullYear(), a.getMonth(), a.getFullYear(), a.getMonth() + 1)
                }
                h()
            }, n[1].onclick = function() {
                var e = o[1].value,
                    t = _(o[0].value),
                    a = new Date(e, t - 1, 1),
                    i = new Date(a.getTime() - 864e5);
                new Date(i.getFullYear(), Number(i.getMonth()), i.getDate()) >= new Date(S) && u(e, t - 2, e, t - 1), h()
            }, n[6].onclick = function() {
                var e = o[1].value,
                    t = _(o[0].value);
                e == j.substring(0, 4) && t == j.substring(5, 7) || u(e, t, e, Number(t) + 1), h()
            }, n[5].onclick = function() {
                var e = o[1].value,
                    t = _(o[0].value);
                if (e < j.substring(0, 4)) u(Number(e) + 1, t - 1, Number(e) + 1, t);
                else {
                    var a = new Date(j);
                    u(a.getFullYear(), a.getMonth(), a.getFullYear(), a.getMonth() + 1)
                }
                h()
            }, n[8].onclick = function() {
                var e = o[2].value,
                    t = _(o[3].value);
                new Date(e, t - 1, 1) <= new Date(j) && u(e, t - 1, e, t), h()
            }, n[7].onclick = function() {
                var e = o[1].value,
                    t = _(o[0].value);
                if (e < j.substring(0, 4)) u(Number(e) + 1, t - 1, Number(e) + 1, t);
                else {
                    var a = new Date(j);
                    u(a.getFullYear(), a.getMonth(), a.getFullYear(), a.getMonth() + 1)
                }
                h()
            };
            var Q = new Array("", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月", "一月"),
                B = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"),
                $ = (-1 != navigator.userAgent.indexOf("MSIE") && window.opera, [19416, 19168, 42352, 21717, 53856, 55632, 91476, 22176, 39632, 21970, 19168, 42422, 42192, 53840, 119381, 46400, 54944, 44450, 38320, 84343, 18800, 42160, 46261, 27216, 27968, 109396, 11104, 38256, 21234, 18800, 25958, 54432, 59984, 28309, 23248, 11104, 100067, 37600, 116951, 51536, 54432, 120998, 46416, 22176, 107956, 9680, 37584, 53938, 43344, 46423, 27808, 46416, 86869, 19872, 42448, 83315, 21200, 43432, 59728, 27296, 44710, 43856, 19296, 43748, 42352, 21088, 62051, 55632, 23383, 22176, 38608, 19925, 19152, 42192, 54484, 53840, 54616, 46400, 46496, 103846, 38320, 18864, 43380, 42160, 45690, 27216, 27968, 44870, 43872, 38256, 19189, 18800, 25776, 29859, 59984, 27480, 21952, 43872, 38613, 37600, 51552, 55636, 54432, 55888, 30034, 22176, 43959, 9680, 37584, 51893, 43344, 46240, 47780, 44368, 21977, 19360, 42416, 86390, 21168, 43312, 31060, 27296, 44368, 23378, 19296, 42726, 42208, 53856, 60005, 54576, 23200, 30371, 38608, 19415, 19152, 42192, 118966, 53840, 54560, 56645, 46496, 22224, 21938, 18864, 42359, 42160, 43600, 111189, 27936, 44448]),
                z = ["小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"],
                R = [0, 21208, 43467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758],
                W = "日一二三四五六七八九十",
                J = ["正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"],
                Z = "初十廿卅",
                G = {
                    "0101": "*1元旦",
                    "0501": "*1劳动",
                    1001: "*7国庆"
                },
                V = {
                    "0101": "*6春节",
                    "0115": "*1元宵",
                    "0505": "*1端午",
                    "0815": "*1中秋",
                    "0100": "除夕"
                },
                K = function() {
                    function e(e) {
                        return e % 4 == 0 && e 0 != 0 || e @0 == 0
                    }

                    function t(t, a) {
                        return [31, e(t) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][a]
                    }

                    function a(e, t) {
                        return e.setDate(e.getDate() + t), e
                    }

                    function i(e, i) {
                        var n = e.solarMonth - 2;
                        0 == e.solarMonth ? n = 11 : 1 == e.solarMonth && (n = 10);
                        var o = new y(new Date(e.solarYear, n, 1)),
                            s = o.solarWeekDay,
                            l = new y(new Date(e.solarYear, e.solarMonth, 1)),
                            c = l.solarWeekDay,
                            u = 0,
                            d = new y(new Date(e.solarYear, e.solarMonth - 1, 1)),
                            h = d.solarWeekDay;
                        if (C) r.lines = Math.ceil((h + t(e.solarYear, e.solarMonth - 1)) / 7);
                        else if (0 == i) {
                            var f = Math.ceil((h + t(e.solarYear, e.solarMonth - 1)) / 7),
                                p = Math.ceil((c + t(e.solarYear, 12 == Number(e.solarMonth) ? 0 : Number(e.solarMonth))) / 7);
                            r.lines = f > p ? f : p
                        } else if (1 == i) {
                            var f = Math.ceil((h + t(e.solarYear, e.solarMonth - 1)) / 7),
                                p = Math.ceil((s + t(e.solarYear, n)) / 7);
                            r.lines = f > p ? f : p
                        } else r.lines = 6;
                        for (var v = 0; v < r.dateArray.length; v++)
                            if (0 != d.restDays && (u = d.restDays), u > 0 && (d.isRest = !0), h-- > 0 || d.solarMonth != e.solarMonth) r.dateArray[v] = null;
                            else {
                                var i = new y(new Date);
                                d.solarYear == i.solarYear && d.solarMonth == i.solarMonth && d.solarDate == i.solarDate && (d.isToday = !0), r.dateArray[v] = d, d = new y(a(d.date, 1)), u--
                            }
                    }
                    var r = {};
                    return r.lines = 0, r.dateArray = new Array(42), {
                        init: function(e, t) {
                            i(e, t)
                        },
                        getJson: function() {
                            return r
                        }
                    }
                }(),
                X = function() {
                    function t(t) {
                        var a = 1 == t ? r[6] : r[13],
                            n = K.getJson(),
                            o = n.dateArray,
                            s = "2" == E ? 38 : 24;
                        a.style.height = n.lines * s + 2 + "px", a.innerHTML = "";
                        for (var l = 0; l < o.length; l++)
                            if (null != o[l]) {
                                var u = o[l].solarYear + "-" + o[l].solarMonth + "-" + o[l].solarDate,
                                    d = M ? u : u + " " + o[l].solarWeekDayInChinese;
                                d = x ? x + d : d, d = T ? d + T : d;
                                var h = c("DIV");
                                o[l].isToday && (h.style.border = "1px solid #a5b9da", h.style.background = O), h.className = "cell", "2" == E && (h.style.height = "36px"), h.style.left = l % 7 == 0 ? "0px" : l % 7 * 42 + 3 + "px", h.style.top = Math.floor(l / 7) * s + 5 + "px", o[l].calendarDate >= new Date(S) && o[l].calendarDate <= new Date(j) && (h.onclick = function(t) {
                                    return function() {
                                        e(b).val(t), i.hide(), e(b).change()
                                    }
                                }(d), h.style.cursor = "pointer");
                                var f = c("DIV");
                                f.className = "so", f.style.color = l % 7 == 0 || l % 7 == 6 || o[l].isRest || o[l].isToday ? L : N, o[l].calendarDate >= new Date(S) && o[l].calendarDate <= new Date(j) || (f.style.color = q), "3" == E ? (o[l].solarFestival ? f.innerHTML = o[l].solarFestival : o[l].lunarFestival ? f.innerHTML = o[l].lunarFestival : o[l].isToday ? f.innerHTML = "今天" : f.innerHTML = "0" == o[l].solarDate.substring(0, 1) ? o[l].solarDate.substring(1) : o[l].solarDate, f.numHTML = "0" == o[l].solarDate.substring(0, 1) ? o[l].solarDate.substring(1) : o[l].solarDate) : (f.innerHTML = "0" == o[l].solarDate.substring(0, 1) ? o[l].solarDate.substring(1) : o[l].solarDate, f.numHTML = "0" == o[l].solarDate.substring(0, 1) ? o[l].solarDate.substring(1) : o[l].solarDate);
                                var p = c("SPAN");
                                if (p.className = "holiday", H[u] ? (p.innerHTML = H[u], h.appendChild(p)) : Y[u] && (p.innerHTML = Y[u], h.appendChild(p)), h.appendChild(f), "2" == E) {
                                    var v = c("DIV");
                                    o[l].calendarDate >= new Date(S) && o[l].calendarDate <= new Date(j) ? v.style.color = P : v.style.color = q, v.innerHTML = o[l].showInLunar, h.appendChild(v)
                                }
                                a.appendChild(h)
                            }
                    }
                    return {
                        draw: function(e) {
                            0 == e ? t(e) : 1 == e ? t(1) : (t(e), t(1))
                        },
                        resetYM: function(e, t) {
                            o[0].value = Q[Number(e.solarMonth)], o[1].value = e.solarYear, o[2].value = t.solarYear, o[3].value = Q[Number(t.solarMonth)]
                        }
                    }
                }(),
                ee = new y(new Date);
            if (K.init(ee, 0), X.draw(1), C) i[0].className = "cal-wrap cal-one";
            else {
                n[6].style.display = "none", n[5].style.display = "none";
                var te = new Date,
                    ae = new y(new Date(te.getFullYear(), te.getMonth() + 1, te.getDate()));
                K.init(ae, 1), X.draw(0)
            }
            if (I || (r[14].style.display = "none"), U) {
                n[2].onclick = function() {
                    if (s[1].getElementsByTagName("li")[0].innerHTML < 1902 || "auto" == this.style.cursor) return void(r[4].style.display = "none");
                    g(1, s[1].getElementsByTagName("li")[0].innerHTML - 5)
                }, n[3].onclick = function() {
                    if (s[1].getElementsByTagName("li")[0].innerHTML > 2040 || "auto" == this.style.cursor) return void(r[4].style.display = "none");
                    g(1, Number(s[1].getElementsByTagName("li")[0].innerHTML) + 15)
                }, n[4].onclick = function() {
                    s[1].parentElement.style.display = "none"
                }, n[9].onclick = function() {
                    if (s[3].getElementsByTagName("li")[0].innerHTML < 1902 || "auto" == this.style.cursor) return void(r[10].style.display = "none");
                    g(3, s[3].getElementsByTagName("li")[0].innerHTML - 5)
                }, n[10].onclick = function() {
                    if (s[3].getElementsByTagName("li")[0].innerHTML > 2040 || "auto" == this.style.cursor) return void(r[10].style.display = "none");
                    g(3, Number(s[3].getElementsByTagName("li")[0].innerHTML) + 15)
                }, n[11].onclick = function() {
                    s[3].parentElement.style.display = "none"
                }, o[0].onfocus = function() {
                    s[0].style.display = "block", m(0), r[4].style.display = "none"
                }, o[0].onblur = function() {
                    s[0].style.display = "none"
                };
                for (var ie = s[0].getElementsByTagName("li"), re = 0; re < ie.length; re++) ie[re].onclick = function() {
                    if ("auto" == this.style.cursor) return s[0].style.display = "none", void(s[4].style.display = "none");
                    var e = o[1].value,
                        t = _(this.innerHTML) + "";
                    t = 1 == t.length ? "0" + t : t, u(e, t - 1, e, t), s[0].style.display = "none", h()
                };
                m(0), o[1].onfocus = function() {
                    g(1, Number(o[1].value)), r[4].style.display = "block"
                }, o[1].onblur = function() {
                    r[4].style.display = "none"
                }, r[4].onmouseover = function() {
                    o[1].onblur = function() {}
                }, r[4].onmouseout = function() {
                    o[1].onblur = function() {
                        r[4].style.display = "none"
                    }
                }, s[0].onmouseover = function() {
                    o[0].onblur = function() {}
                }, s[0].onmouseout = function() {
                    o[0].onblur = function() {
                        s[0].style.display = "none"
                    }
                }, o[3].onfocus = function() {
                    m(4), s[4].style.display = "block", r[10].style.display = "none"
                }, o[3].onblur = function() {
                    s[4].style.display = "none"
                };
                for (var ne = s[4].getElementsByTagName("li"), re = 0; re < ne.length; re++) ne[re].onclick = function() {
                    if ("auto" == this.style.cursor) return s[0].style.display = "none", void(s[4].style.display = "none");
                    var e = o[2].value,
                        t = _(this.innerHTML) + "";
                    t = 1 == t.length ? "0" + t : t, u(e, t - 2, e, t - 1), s[4].style.display = "none", h()
                };
                m(4), o[2].onfocus = function() {
                    g(3, Number(o[2].value)), r[10].style.display = "block"
                }, o[2].onblur = function() {
                    r[10].style.display = "none"
                }, r[10].onmouseover = function() {
                    o[2].onblur = function() {}
                }, r[10].onmouseout = function() {
                    o[2].onblur = function() {
                        r[10].style.display = "none"
                    }
                }, s[4].onmouseover = function() {
                    o[3].onblur = function() {}
                }, s[4].onmouseout = function() {
                    o[3].onblur = function() {
                        s[4].style.display = "none"
                    }
                };
                for (var re = 0; re < 4; re++) o[re].disabled = !1, o[re].style.cursor = "pointer"
            }
            var oe = new Date;
            o[0].value = Q[oe.getMonth() + 1], o[1].value = oe.getFullYear(), o[2].value = 11 == oe.getMonth() ? oe.getFullYear() + 1 : oe.getFullYear(), o[3].value = Q[oe.getMonth() + 2],
                function() {
                    r[4].style.display = "none", t = !0;
                    var a = C ? 261 : 522,
                        n = document.body.clientWidth - a - 10,
                        o = e(b).offset().top,
                        s = e(b).offset().left;
                    s = s >= n ? n : s;
                    var l = e(b).innerHeight();
                    i.css("left", s), i.css("top", o + l);
                    var c = d(e(b).val());
                    c && c.length > 4 && c.substring(0, 4) > 1900 && c.substring(0, 4) < 2051 && u(c.substring(0, 4), c.substring(5, 7) - 1, c.substring(0, 4), c.substring(5, 7)), h(), i.show()
                }()
        }, e.jcalendar.defaultOptions = {
            isSingle: !0,
            showFormat: !0,
            formatBeforeInfo: "",
            formatAfterInfo: "",
            startDate: "1901-01-01",
            endDate: "2050-12-31",
            isTwoRows: "3",
            isTodayBlock: !0,
            isYearMonthDisabled: !0,
            condition: [!1, "#query_H", "active", "2050-12-31"],
            restColor: "#c60b02",
            noRestColor: "#313131",
            todayClickColor: "#c1d9ff",
            noClickColor: "#aaa",
            clickByYearMonth: "#003784",
            lunarColor: "#666",
            closeCalendar: function() {},
            onpicked: function() {}
        }, e.fn.jcalendar = function() {
            var t = Array.prototype.slice.call(arguments);
            return new e.jcalendar(this, t[0])
        }
    }),
    function(e) {
        "function" == typeof define && define.amd ? define("core/common/date", ["jquery"], e) : e(jQuery)
    }(function(e) {
        e(".icon-date").each(function(t, a) {
            e(this).click(function(t) {
                e("#" + e(this).attr("data-click")).focus()
            })
        }), jQuery.extend({
            datepicker: function(t, a, i) {
                e(t).focus(function() {
                    e(t).jcalendar({
                        isSingle: !0,
                        startDate: a,
                        endDate: i,
                        onpicked: function() {
                            e(t).blur(), e(t).hasClass("inp-txt_select") || e(t).addClass("inp-txt_select"), e(t).hasClass("error") && e(t).removeClass("error")
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dateTrain: function(t, a, i, r) {
                e(t).focus(function() {
                    e(t).jcalendar({
                        isSingle: !1,
                        startDate: a,
                        endDate: i,
                        onpicked: function() {
                            "#go_date" == t && e("#from_date").val(e("#go_date").val()), e(t).blur(), e(t).hasClass("inp-txt_select") || e(t).addClass("inp-txt_select"), e(t).hasClass("error") && e(t).removeClass("error")
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            GetDateStr: function(e) {
                var t = new Date;
                return t.setDate(t.getDate() + e),
                t.getFullYear() + "-" + (t.getMonth() + 1 < 10 ? "0" + (t.getMonth() + 1) : t.getMonth() + 1) + "-" + (t.getDate() < 10 ? "0" + t.getDate() : t.getDate())
            },
            dianzifromStr: function(t, a, i) {
                e(t).focus(function() {
                    e(t).jcalendar({
                        isSingle: !1,
                        startDate: null,
                        endDate: null,
                        onpicked: function() {
                            timeChangetype(e("#travelFromDate").val().replace(/-/g, "/")) > timeChangetype(e("#travelToDate").val().replace(/-/g, "/")) && e("#travelToDate").val(e("#travelFromDate").val()), e(t).blur()
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dianzitoStr: function(t, a, i) {
                e(t).focus(function() {
                    e(t).jcalendar({
                        isSingle: !1,
                        startDate: e("#travelFromDate").val() || GetDateStr(0),
                        endDate: null,
                        onpicked: function() {
                            e(t).blur()
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dateRefundDingStr: function(a, i) {
                e(a).focus(function() {
                    t = !0, e(a).jcalendar({
                        isSingle: !1,
                        startDate: formatDate(new Date),
                        onpicked: function() {
                            if (e("#noTripFromDate").val()) {
                                timeChangetype(e("#noTripFromDate").val().replace(/-/g, "/")) > timeChangetype(e("#noTripToDate").val().replace(/-/g, "/")) && e("#noTripToDate").val(e("#noTripFromDate").val())
                            }
                            e(a).blur(), e(a).hasClass("inp-txt_select") || e(a).addClass("inp-txt_select"), e(a).hasClass("error") && e(a).removeClass("error")
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dateRefundDingEnd: function(a, i) {
                e(a).focus(function() {
                    t = !0, e(a).jcalendar({
                        isSingle: !1,
                        startDate: formatDate(new Date),
                        onpicked: function() {
                            e(a).blur(), e(a).hasClass("inp-txt_select") || e(a).addClass("inp-txt_select"), e(a).hasClass("error") && e(a).removeClass("error"), timeChangetype(e("#noTripFromDate").val().replace(/-/g, "/")) > timeChangetype(e("#noTripToDate").val().replace(/-/g, "/")) && e("#noTripFromDate").val(e("#noTripToDate").val())
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dateHistoryStr: function(a, i) {
                e(a).focus(function() {
                    t = !0, e(a).jcalendar({
                        isSingle: !1,
                        startDate: GetDateStr(i ? -29 : -1),
                        endDate: GetDateStr(i ? -1 : 29),
                        onpicked: function() {
                            if (e("#historyFromDate").val()) {
                                timeChangetype(e("#historyFromDate").val().replace(/-/g, "/")) > timeChangetype(e("#historyToDate").val().replace(/-/g, "/")) && e("#historyToDate").val(e("#historyFromDate").val())
                            }
                            e(a).blur(), e(a).hasClass("inp-txt_select") || e(a).addClass("inp-txt_select"), e(a).hasClass("error") && e(a).removeClass("error")
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            dateHistoryEnd: function(a, i) {
                e(a).focus(function() {
                    t = !0, e(a).jcalendar({
                        isSingle: !1,
                        startDate: i ? e("#historyFromDate").val() || GetDateStr(-29) : e("#historyFromDate").val() || GetDateStr(-1),
                        endDate: GetDateStr(i ? -1 : 29),
                        onpicked: function() {}
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            },
            noTripDPStr: function(t, a, i) {
                e(t).focus(function() {
                    e(t).jcalendar({
                        isSingle: !1,
                        endDate: GetDateStr(0),
                        onpicked: function() {
                            if ("#noTripToDate" == t) {
                                var a = timeChangetype(e("#noTripFromDate").val().replace(/-/g, "/")),
                                    i = timeChangetype(e("#noTripToDate").val().replace(/-/g, "/"));
                                a > i && e("#noTripFromDate").val(e("#noTripToDate").val())
                            }
                            if ("#noTripFromDate" == t) {
                                var a = timeChangetype(e("#noTripFromDate").val().replace(/-/g, "/")),
                                    i = timeChangetype(e("#noTripToDate").val().replace(/-/g, "/"));
                                a > i && e("#noTripToDate").val(e("#noTripFromDate").val())
                            }
                            e(t).blur(), e(t).hasClass("inp-txt_select") || e(t).addClass("inp-txt_select"), e(t).hasClass("error") && e(t).removeClass("error")
                        }
                    })
                }).blur(function() {
                    e(".cal-wrap").hide()
                })
            }
        });
        var t = !1
    }),
    function(e) {
        "function" == typeof define && define.amd ? define("core/plugin/confirm", ["jquery"], e) : e(jQuery)
    }(function(e) {
        window.DZP = window.DZP || {}, window.DZP.confirm = function(t, a, i) {
            function r() {
                M.append(E.append(U)).append(x.append(m)).append(T.append(_)).append(c(b)).append(N), k.attr("id", D).append(C).append(M), e("body").append(k);
                var t = navigator.appName,
                    a = navigator.appVersion,
                    i = a.split(";");
                if (i[1]) {
                    var r = i[1].replace(/[ ]/g, "");
                    "Microsoft Internet Explorer" == t && "MSIE8.0" == r && M.css({
                        "margin-left": -M.outerWidth() / 2 + "px",
                        "margin-top": -M.outerHeight() / 2 + "px"
                    })
                }
                "系统繁忙，请稍后重试！" == v.msg.tit && P.click(function() {
                    window.location.href = htmlHref.index
                })
            }

            function n() {
                P.click(o), e(window).bind("keydown", function(t) {
                    13 == t.keyCode && 1 == e("#" + D).length && o()
                }), F.click(s), E.click(l)
            }

            function o() {
                e(this);
                v.onOk(), e("#" + D).remove(), v.onClose(h.ok)
            }

            function s() {
                e(this);
                v.onCancel(), e("#" + D).remove(), v.onClose(h.cancel)
            }

            function l() {
                e("#" + D).remove(), v.onClose(h.close), e(window).unbind("keydown")
            }

            function c(t) {
                var a = S;
                return e.each(A, function(e, i) {
                    d[e] == (t & d[e]) && a.append(i)
                }), a
            }

            function u() {
                var t = "pop_" + (new Date).getTime() + parseInt(1e5 * Math.random());
                return e("#" + t).length > 0 ? u() : t
            }
            var d = window.DZP.confirm.btnEnum,
                h = window.DZP.confirm.eventEnum,
                f = {
                    info: {
                        title: "信息",
                        icon: "",
                        btn: d.ok
                    },
                    success: {
                        title: "成功",
                        icon: "success",
                        btn: d.ok
                    },
                    error: {
                        title: "错误",
                        icon: "error",
                        btn: d.ok
                    },
                    confirm: {
                        title: "提示",
                        icon: "plaint-fill",
                        btn: d.okcancel
                    },
                    warning: {
                        title: "警告",
                        icon: "doubt",
                        btn: d.ok
                    },
                    custom: {
                        title: "",
                        icon: "",
                        btn: d.ok
                    }
                },
                p = a ? a instanceof Object ? a : f[a] || {} : {},
                v = e.extend(!0, {
                    width: "440",
                    title: "",
                    icon: "",
                    iconUrl: "",
                    msg: {
                        tit: "",
                        info: "",
                        tips: ""
                    },
                    ft: "",
                    btn: d.ok,
                    ok: "确定",
                    cancel: "取消",
                    onOk: e.noop,
                    onCancel: e.noop,
                    onClose: e.noop
                }, p, i),
                _ = t || "",
                m = e("<div>").addClass("modal-tit").text(v.title),
                g = v.icon,
                y = v.iconUrl,
                w = y || (g ? e("<i>").addClass("icon").addClass("icon-" + g) : ""),
                b = v.btn,
                D = u(),
                k = e("<div>").addClass("dzp-confirm"),
                C = e("<div>").addClass("mask"),
                M = e("<div>").addClass("modal").css({
                    display: "block",
                    position: "fixed",
                    width: v.width + "px",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    "-webkit-transform": "translate(-50%, -50%)"
                }),
                x = e("<div>").addClass("modal-hd"),
                T = e("<div>").addClass("modal-bd"),
                S = e("<div>").addClass("modal-ft"),
                j = e("<div>").addClass("message"),
                I = e("<div>").addClass("msg-ico"),
                O = e("<div>").addClass("msg-con"),
                q = e("<h2>").addClass("msg-tit").html(v.msg.tit),
                L = e("<div>").addClass("msg-info").html(v.msg.info),
                N = v.ft ? e("<div>").addClass("modal-ft-tips").html(v.ft) : "",
                F = e("<a>").addClass("btn").addClass("cancel").text(v.cancel),
                P = e("<a>").addClass("btn").addClass("btn-primary").addClass("ok").text(v.ok),
                E = e("<a>").attr("href", "javascript:;").attr("title", "关闭").addClass("modal-close"),
                U = e("<i>").addClass("icon").addClass("icon-close");
            T = g ? T.append(j.append(I.append(w)).append(O.append(q).append(L))).append(v.msg.tips) : T.append(v.msg.tips);
            var A = {
                cancel: F,
                ok: P
            };
            ! function() {
                e(".dzp-confirm").remove(), r(), n()
            }()
        }, window.DZP.confirm.btnEnum = {
            ok: parseInt("0001", 2),
            cancel: parseInt("0010", 2),
            okcancel: parseInt("0011", 2)
        }, window.DZP.confirm.eventEnum = {
            ok: 1,
            cancel: 2,
            close: 3
        }, window.DZP.confirm.typeEnum = {
            info: "info",
            success: "success",
            error: "error",
            confirm: "confirm",
            warning: "warning",
            custom: "custom"
        }
    }), define("index/index_init", ["jquery"], function(e) {
    function t() {
        var t = navigator.appName,
            i = navigator.appVersion,
            r = i.split(";");
        if (r[1]) {
            var n = r[1].replace(/[ ]/g, "");
            "Microsoft Internet Explorer" == t && "MSIE7.0" == n ? window.location.href = htmlHref.browserForie : "Microsoft Internet Explorer" == t && "MSIE6.0" == n && (window.location.href = htmlHref.browserForie)
        }
        jQuery.support.cors = !0, a(), e(".index_notice").on("click", function() {
            e(".index_notice").attr("href", noticeUrl)
        })
    }

    function a() {
        loadingShow(), e.ajax({
            url: personal_welcome_url,
            type: "POST",
            timeout: 1e4,
            success: function(t) {
                if (loadingHide(), t.data) {
                    var a = JSON.parse(JSON.stringify(t.data));
                    if (a.user_name && a.user_regard) {
                        var i = '<div class="welcome-tit"><img src="../images/center/noticepic.png" alt="" class="welcome-notice"><strong class="welcome-name">' + a.user_name + "</strong><span>" + a.user_regard + "</span></div>";
                        if (i += '<div class="welcome-con"><p>欢迎您登录中国铁路客户服务中心网站。<br />', i += '<span style="color: red;">如果您的密码在其他网站也使用，建议您修改本网站密码。</span><br />', a._is_active && "N" == a._is_active && (i += "如果您要接收12306的服务邮件，请<a id='link_for_reSendEmail' href='javascript:;' class = 'txt-primary underline'>验证邮箱</a>。<br />"), "Y" == a._is_needModifyPassword && (i += "您设置的密码安全级别较低，强烈建议您<a id='link_for_changePassword' href='javascript:;' class = 'txt-primary underline'>修改密码</a>。<br/>"), a.notify_SESSION && "Y" == a.notify_SESSION && (i += "<span>本次登录成功！该用户已在其他地点登录，前次登录将被强制退出。</span><br />"), a.isCanRegistMember && (i += '点击<a id="isStationMember" href="javascript:;" class = "txt-primary underline">成为会员</a><br />'), a.notify_TWO_2 && "" != a.notify_TWO_2) {
                            if ("完成手机双向核验，即可使用手机号码直接登录的新服务，解除您遗忘用户名的烦恼。" != a.notify_TWO_2) {
                                var r = a.notify_TWO_2;
                                window.DZP.confirm(r, window.DZP.confirm.typeEnum.info, {
                                    width: "440",
                                    height: "160",
                                    title: "温馨提示"
                                })
                            }
                            i += "<span style='color:red;'>", i += a.notify_TWO_2, i += "</span><br />"
                        } else i += "如果您需要预订车票，请您点击<a id='link_for_ticket' class = 'txt-primary underline' href='javascript:;'>车票预订</a>。";
                        "Y" == a.to_12306 && (i += '<div><br></br>恭喜您在铁路旅客服务质量问卷调查活动中获奖，为确保您的奖励积分使用安全，请您点击<a id="link_for_needKnow" href="javascript:;" class="txt-primary underline"><span>兑奖须知</span></a>补充并确认相关信息。</div>'), "Y" == a.resetMemberPwd && (i += '<div><br></br>您好，原积分用户需重置交易密码，请您点击<a id="resetMemberPwd" href="javascript:;" class="txt-primary underline"><span>重置密码</span></a></div>'), "YN" == a.isSuperUser && (i += "<div>S</div>"), i += "</p></div>", e(".welcome_data").html(i), e(".tips-box").show()
                    }
                    if (a.qr_code_url) {
                        var n = static_url_path + "/index/requestWechatQr";
                        e("#weixinImg").html('<div class="code-pic"><img src=' + n + '></div><div class="code-txt">使用微信扫一扫，可通过<br>微信公众号接收12306行程通知</div>')
                    }
                    if (a.if_show_ali_qr_code) {
                        var n = static_url_path + "/index/requestAliQr";
                        e("#aliImg").html('<div class="code-pic"><img src=' + n + '></div><div class="code-txt">使用支付宝扫一扫，可通过<br>支付宝通知提醒接收12306行程通知</div>')
                    }
                }
                e("#link_for_reSendEmail").on("click", function() {
                    e.ajax({
                        url: static_url_path + "/index/reSendEmail",
                        type: "post",
                        success: function(e) {
                            if (e.data) {
                                var t = e.data._email ? e.data._email : "";
                                window.DZP.confirm("", window.DZP.confirm.typeEnum.confirm, {
                                    height: "190",
                                    title: "验证邮箱",
                                    msg: {
                                        tit: "验证邮件已发送，请您登录邮箱<br>" + t + "完成验证！"
                                    },
                                    onOk: function() {
                                        window.location.href = static_url_path + "/view/information.html"
                                    }
                                })
                            } else {
                                window.DZP.confirm("验证邮件发送失败！", window.DZP.confirm.typeEnum.confirm, {
                                    title: "验证邮箱",
                                    width: "400",
                                    height: "150",
                                    onOk: function() {
                                        window.location.href = static_url_path + "/view/information.html"
                                    }
                                })
                            }
                        },
                        error: function() {}
                    })
                }), e("#link_for_changePassword").on("click", function() {
                    window.location.href = static_url_path + "/userSecurity/loginPwd?req_flag=init"
                }), e("#isStationMember").click(function() {
                    e.ajax({
                        url: static_url_path + "/index/isStationMember",
                        type: "post",
                        success: function(t) {
                            var a = t.data;
                            a ? "1" == a.flag ? (e("#isStationMember").attr("target", "_blank"), window.location.href = "https://cx.12306.cn/tlcx/register.html?id=1") : (e("#isStationMember").attr("target", "_blank"), window.location.href = "https://cx.12306.cn/tlcx/register.html?id=0") : window.DZP.confirm("", window.DZP.confirm.typeEnum.confirm, {
                                title: "提示",
                                msg: {
                                    tit: "系统繁忙,请稍后再试"
                                },
                                btn: 1
                            })
                        },
                        error: function() {
                            window.DZP.confirm("", window.DZP.confirm.typeEnum.confirm, {
                                title: "提示",
                                msg: {
                                    tit: "系统繁忙,请稍后再试"
                                },
                                btn: 1
                            })
                        }
                    })
                }), e("#link_for_ticket").on("click", function() {
                    window.location.href = static_url_path + "/leftTicket/init"
                }), e("#link_for_needKnow").on("click", function() {
                    e.ajax({
                        url: static_url_path + "/index/checkIsOrNotMember",
                        type: "post",
                        success: function(e) {
                            if (e.data.flag) window.location.href = static_url_path + "/index/preAddMember";
                            else {
                                var t = e.data.message;
                                window.DZP.confirm(t, window.DZP.confirm.typeEnum.confirm, {
                                    title: "积分兑换",
                                    width: "400",
                                    height: "150"
                                })
                            }
                        },
                        error: function() {
                            window.DZP.confirm("积分兑换失败！", window.DZP.confirm.typeEnum.confirm, {
                                title: "积分兑换",
                                width: "400",
                                height: "150"
                            })
                        }
                    })
                }), e("#resetMemberPwd").on("click", function() {
                    window.location.href = static_url_path + "/userIntegration/gotoResetMemPwd"
                })
            },
            error: function(e) {
                loadingHide(), window.DZP.confirm("", window.DZP.confirm.typeEnum.confirm, {
                    title: "提示",
                    msg: {
                        tit: "系统繁忙,请稍后再试"
                    },
                    btn: 1
                })
            }
        })
    }
    return {
        initialize: function() {
            t()
        }
    }
}), define("index/app", ["jquery", "g/g-header", "g/g-footer", "g/g-href", "core/common/jquery.SuperSlide", "core/common/mUtils", "core/common/common", "core/common/data.jcokies", "core/common/url_config", "core/common/data.jcalendar", "core/common/date", "core/plugin/confirm", "underscore", "index/index_init"], function(e, t, a, i, r, n, o, s, l, c, u, d, h, f) {
    function p() {
        t.initialize(), e("#J-index").removeClass("active"), e("#J-chepiao").addClass("active"), e("#gerenzhongxin h2").addClass("active"), isOver && (f.initialize(), a.initialize(), i.initialize())
    }
    return {
        initialize: p
    }
}), require.config({
    baseUrl: "../personalJS/",
    shim: {
        jquery: {
            exports: "$"
        },
        underscore: {
            exports: "underscore"
        }
    },
    paths: {
        jquery: "core/lib/jquery.min",
        underscore: "core/lib/underscore"
    },
    waitSeconds: 0
}), require(["index/app"], function(e) {
    new e.initialize
}), define("index/main", function() {});