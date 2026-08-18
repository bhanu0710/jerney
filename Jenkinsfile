pipeline {

    agent any

    tools {
        nodejs 'nodejs'
    }

    environment {

        SCANNER_HOME = tool 'sonarqube'

        BACKEND_IMAGE = "bhanu0710/jerney-backend"

        FRONTEND_IMAGE = "bhanu0710/jerney-frontend"

    }

    stages {

        stage("Initialize") {

            steps {

                cleanWs()

                checkout scm

                script {

                    backend = load 'jenkins/backend.groovy'

                    frontend = load 'jenkins/frontend.groovy'

                    docker = load 'jenkins/docker.groovy'

                    security = load 'jenkins/security.groovy'

                    gitops = load 'jenkins/gitops.groovy'

                    utils = load 'jenkins/utils.groovy'

                    env.IMAGE_TAG = utils.gitSha()

                    echo "Tag : ${IMAGE_TAG}"

                }

            }

        }

        stage("Application CI") {

            parallel {

                stage("Backend") {

                    steps {

                        script {

                            backend.install()

                            backend.lint()

                            backend.test()

                        }

                    }

                }

                stage("Frontend") {

                    steps {

                        script {

                            frontend.install()

                            frontend.build()

                            frontend.lint()

                            frontend.test()

                        }

                    }

                }

            }

        }

        stage("SonarQube") {

            steps {

                withSonarQubeEnv("sonar-server") {

                    sh """
                    ${SCANNER_HOME}/bin/sonar-scanner \
                    -Dsonar.projectKey=jerney \
                    -Dsonar.projectName=jerney
                    """

                }

            }

        }

        stage("Quality Gate") {

            steps {

                timeout(time:5,unit:'MINUTES') {

                    waitForQualityGate abortPipeline:true

                }

            }

        }

        stage("Filesystem Scan") {

            parallel {

                stage("Backend Scan") {

                    steps {

                        script {

                            security.filesystemScan("backend")

                        }

                    }

                }

                stage("Frontend Scan") {

                    steps {

                        script {

                            security.filesystemScan("frontend")

                        }

                    }

                }

            }

        }

        stage("Docker Build") {

            parallel {

                stage("Backend Image") {

                    steps {

                        script {

                            backend.buildImage(
                                    BACKEND_IMAGE,
                                    IMAGE_TAG
                            )

                        }

                    }

                }

                stage("Frontend Image") {

                    steps {

                        script {

                            frontend.buildImage(
                                    FRONTEND_IMAGE,
                                    IMAGE_TAG
                            )

                        }

                    }

                }

            }

        }

        stage("Image Scan") {

            parallel {

                stage("Backend") {

                    steps {

                        script {

                            security.imageScan(
                                    "${BACKEND_IMAGE}:${IMAGE_TAG}"
                            )

                        }

                    }

                }

                stage("Frontend") {

                    steps {

                        script {

                            security.imageScan(
                                    "${FRONTEND_IMAGE}:${IMAGE_TAG}"
                            )

                        }

                    }

                }

            }

        }

        stage("Push Images") {

            steps {

                script {

                    docker.login()

                    docker.push(
                            BACKEND_IMAGE,
                            IMAGE_TAG
                    )

                    docker.push(
                            FRONTEND_IMAGE,
                            IMAGE_TAG
                    )

                    docker.logout()

                }

            }

        }

        stage("GitOps") {

            steps {

                script {

                    gitops.updateBackend(
                            "${BACKEND_IMAGE}:${IMAGE_TAG}"
                    )

                    gitops.updateFrontend(
                            "${FRONTEND_IMAGE}:${IMAGE_TAG}"
                    )

                    gitops.push()

                }

            }

        }

    }

    post {

        success {

            echo "Pipeline Successful"

        }

        failure {

            echo "Pipeline Failed"

        }

        always {

            sh 'docker system prune -af || true'

            cleanWs()

        }

    }

}
