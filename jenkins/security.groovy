def filesystemScan(String path) {

    sh """
    mkdir -p reports

    trivy fs \
      --severity HIGH,CRITICAL \
      --format table \
      --output reports/${path}-trivy-fs.txt \
      ${path}
    """

}

def imageScan(String image) {

    def reportName = image
            .replace("/", "-")
            .replace(":", "-")

    sh """
    mkdir -p reports

    trivy image \
      --severity HIGH,CRITICAL \
      --format table \
      --output reports/${reportName}.txt \
      ${image}
    """

}

return this
