layout 'ui/layout.tpl',
    headContents: contents {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        script(src: "/js/lib/jquery.min.js"){}
        script(src: "/js/lib/chart.js"){}
        script(src: "/js/my-lib.js"){}
        title('${title}')
    },
    bodyContents: contents {
        h2 ('A Groovy View with Spring MVC')
        div ("msg: $msg")
        div ("time: $time")
    }