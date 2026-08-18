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
                    
                        def files = utils.changedFiles()
                    
                        env.BACKEND_CHANGED = utils.backendChanged(files).toString()
                    
                        env.FRONTEND_CHANGED = utils.frontendChanged(files).toString()
                    
                        env.MANIFEST_CHANGED = utils.manifestsChanged(files).toString()
                    
                        echo "Image Tag        : ${env.IMAGE_TAG}"
                        echo "Backend Changed  : ${env.BACKEND_CHANGED}"
                        echo "Frontend Changed : ${env.FRONTEND_CHANGED}"
                        echo "Manifest Changed : ${env.MANIFEST_CHANGED}"

                }

            }

        }

        stage("Application CI") {

            parallel {

                stage("Backend") {

                    when {
                       expression {
                         env.BACKEND_CHANGED == "true"
                        }
                     }

                    steps {

                        script {

                            backend.install()

                            backend.lint()

                            backend.test()

                        }

                    }

                }

                stage("Frontend") {

                    when {
                        expression {
                           env.FRONTEND_CHANGED == "true"
                         }
                      }

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

            when {
                expression {
                   env.BACKEND_CHANGED == "true" ||
                   env.FRONTEND_CHANGED == "true"
                 }
               }

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


            when {
                expression {
                    env.BACKEND_CHANGED == "true" ||
                    env.FRONTEND_CHANGED == "true"
                }
            }

            steps {

                timeout(time:5,unit:'MINUTES') {

                    waitForQualityGate abortPipeline:true

                }

            }

        }

        stage("Filesystem Scan") {

            parallel {

                stage("Backend Scan") {

                    when {
                        expression {
                            env.BACKEND_CHANGED == "true"
                        }
                    }

                    steps {

                        script {

                            security.filesystemScan("backend")

                        }

                    }

                }

                stage("Frontend Scan") {

                    when {
                        expression {
                            env.FRONTEND_CHANGED == "true"
                        }
                    }

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

                    when {
                        expression {
                            env.BACKEND_CHANGED == "true"
                          }
                       }

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


                    when {
                         expression {
                              env.FRONTEND_CHANGED == "true"
                           }
                        }

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

                    when {
                        expression {
                            env.BACKEND_CHANGED == "true"
                        }
                    }

                    steps {

                        script {

                            security.imageScan(
                                    "${BACKEND_IMAGE}:${IMAGE_TAG}"
                            )

                        }

                    }

                }

                stage("Frontend") {


                    when {
                        expression {
                            env.FRONTEND_CHANGED == "true"
                        }
                    }

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

            when {
                expression {
                    env.BACKEND_CHANGED == "true" ||
                    env.FRONTEND_CHANGED == "true"
                }
            }

            steps {

                script {

                    docker.login()

                    if(env.BACKEND_CHANGED=="true"){

                        docker.push(
                            BACKEND_IMAGE,
                            IMAGE_TAG
                        )
                    
                    }
                
                    if(env.FRONTEND_CHANGED=="true"){
                    
                        docker.push(
                            FRONTEND_IMAGE,
                            IMAGE_TAG
                        )
                    
                    }

                    docker.logout()

                }

            }

        }

        stage("GitOps") {



            when {

                expression {
                
                      env.BACKEND_CHANGED=="true" ||
                      env.FRONTEND_CHANGED=="true"
                
                        }
                
                    }

            steps {

                script {

                    if (env.BACKEND_CHANGED == "true") {

                        gitops.updateBackend(
                            "${BACKEND_IMAGE}:${IMAGE_TAG}"
                        )
                    
                    }
                    
                    if (env.FRONTEND_CHANGED == "true") {
                    
                        gitops.updateFrontend(
                            "${FRONTEND_IMAGE}:${IMAGE_TAG}"
                        )
                    
                    }

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
