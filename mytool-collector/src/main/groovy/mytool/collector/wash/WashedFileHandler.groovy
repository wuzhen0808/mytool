package mytool.collector.wash

import groovy.transform.CompileStatic

@CompileStatic
interface WashedFileHandler {

    void process(File file, Reader reader, WashedFileLoader.WashedFileLoadContext xContext);
}