layout 'ui/layout.tpl',
    headContents: contents {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        script(src: "/js/lib/jquery.min.js"){}
        script(src: "/js/lib/chart.js"){}
        script(src: "/js/my-lib.js"){}
        title("$title")
    },
    bodyContents: contents {
        h2 ("CorpId: $corpId")

        charts.each { chart ->
            def canvasId = "myChart_${chart.metricType.name}"
            div(style: "width: 1000px;") {
                canvas(id: "$canvasId"){}
            }
            script {
                yieldUnescaped "loadChart('/v1/chart/chart?corpId=$corpId&metricType=${chart.metricType}','$canvasId')"
            }
        }

    }

