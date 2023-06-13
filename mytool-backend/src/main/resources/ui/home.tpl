layout 'ui/layout.tpl',
    headContents: contents {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        script(src: "/js/lib/jquery.min.js"){}
        script(src: "/js/lib/chart.js"){}
        script(src: "/js/my-lib.js"){}
        title("${title}")
    },
    bodyContents: contents {
        h2 ('My Tool')
        input(type: "text", id:"myInput") {

        }
        button(onClick: "openCorpDetail('myInput')"){
            yieldUnescaped '跳转到详情'
        }
        br(){
            yieldUnescaped "浏览历史:"
        }
        recentCorps.each {corp ->
            div() {
                a(href: "/ui/corp/detail?corpId=${corp.id}", target: "_blank"){
                    yieldUnescaped "${corp.name}(${corp.id})"
                }
            }

        }

    }