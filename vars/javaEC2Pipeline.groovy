def call(Map config = [:]) {

    pipeline {
        agent {
            label config.agentLabel ?: 'default'
        }
        environment {
            AWS_REGION = config.awsRegion ?: 'ap-south-1'
            S3_BUCKET  = config.s3Bucket
            APP_NAME   = config.appName ?: 'app'
            S3_PATH    = config.s3Path ?: 'artifacts'
            AWS_CREDS  = config.awsCreds ?: 'aws-creds'
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Read Version from pom.xml') {
                steps {
                    script {
                        def pom = readMavenPom file: 'pom.xml'
                        env.APP_VERSION = pom.version
                        echo "Detected version: ${env.APP_VERSION}"
                    }
                }
            }

            stage('Unit Tests') {
                steps {
                    sh 'mvn test'
                }
            }

            stage('Build Artifact') {
                steps {
                    sh 'mvn clean package -DskipTests'
                }
            }

            stage('Upload to S3') {
                steps {
                    script {
                        def jarName = "${APP_NAME}-${env.APP_VERSION}.jar"
                        def jarPath = "target/${jarName}"

                        withCredentials([[
                            $class: 'AmazonWebServicesCredentialsBinding',
                            credentialsId: AWS_CREDS
                        ]]) {
                            sh """
                              echo "Uploading ${jarPath} to s3://${S3_BUCKET}/${S3_PATH}/"
                              aws s3 cp ${jarPath} s3://${S3_BUCKET}/${S3_PATH}/${jarName}
                            """
                        }
                    }
                }
            }
        }

        post {
            success {
                echo "✅ Uploaded ${APP_NAME}-${env.APP_VERSION}.jar to S3 successfully"
            }
            failure {
                echo "❌ Pipeline failed"
            }
        }
    }
}
