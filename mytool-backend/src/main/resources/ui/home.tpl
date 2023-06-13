layout 'ui/layout.tpl',
    headContents: contents {
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        script(src: "/js/lib/jquery.min.js"){}
        script(src: "/js/lib/chart.js"){}
        script(src: "/js/my-lib.js"){}
        title('${title}')
    },
    bodyContents: contents {
        h2 ('My Tool')
        recentCorps.each {
            div() {
                a(href: "/ui/corp/detail?corpId=${it.id}", target: "_blank", "${it.name}"){

                }
            }

        }

    }