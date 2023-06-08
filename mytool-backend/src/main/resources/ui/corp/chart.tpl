layout 'ui/layout.tpl',
    headContents: contents {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        script(src: "/js/lib/jquery.min.js"){}
        script(src: "/js/lib/chart.js"){}
        script(src: "/js/my-lib.js"){}
        title('${title}')
    },
    bodyContents: contents {
        h2 ('Code: $corpCode')
        div {
            canvas(id: "myChart"){}
        }
        script {
            yieldUnescaped '''
            loadChart('/v1/chart/chart','myChart')
            '''
        }
    }

