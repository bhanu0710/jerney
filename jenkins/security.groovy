def filesystemScan(String path) {
    sh """
    trivy fs \
    --severity HIGH,CRITICAL \
    ${path}
    """
}

def imageScan(String image) {
    sh """
    trivy image \
    --severity HIGH,CRITICAL \
    ${image}
    """
}

return this
