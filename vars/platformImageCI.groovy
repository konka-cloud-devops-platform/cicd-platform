def call(Map config) {
    pipeline {
        agent {
            label config.agentLabel ?: 'any'
        }
        stages {
            stage('Hadolint') {
                steps {
                    sh """
                    docker run --rm \
                    --entrypoint hadolint \
                    -v \$(pwd):/work \
                    -w /work \
                    hadolint/hadolint ${config.dockerfile}
                    """
                }
            }
            stage('Docker Build') {
                steps {
                    sh """
                    docker build \
                        -t ${config.imageName}:${config.imageTag ?: 'latest'} \
                        -f ${config.dockerfile} \
                        .
                    """
                }
            }
        } 
    }
}
// def call(Map config) {
//     pipeline {
//         agent any

//         environment {
//             IMAGE_NAME = config.imageName
//             IMAGE_TAG  = config.imageTag ?: "latest"
//             DOCKERFILE = config.dockerfile
//             ECR_REPO   = config.ecrRepo
//             AWS_REGION = config.awsRegion ?: "ap-south-1"
//         }

//         stages {

//             stage('Hadolint') {
//                 steps {
//                     sh """
//                       docker run --rm \
//                       -v \$(pwd):/work \
//                       hadolint/hadolint hadolint ${DOCKERFILE}
//                     """
//                 }
//             }

//             stage('Docker Build') {
//                 steps {
//                     sh """
//                       docker build \
//                       -t ${IMAGE_NAME}:${IMAGE_TAG} \
//                       -f ${DOCKERFILE} .
//                     """
//                 }
//             }

//             stage('Trivy Scan') {
//                 steps {
//                     script {
//                         org.security.TrivyScan.scan(
//                             "${IMAGE_NAME}:${IMAGE_TAG}"
//                         )
//                     }
//                 }
//             }

//             stage('Generate SBOM') {
//                 steps {
//                     script {
//                         org.security.SBOM.generate(
//                             "${IMAGE_NAME}:${IMAGE_TAG}",
//                             "sbom.json"
//                         )
//                     }
//                 }
//             }

//             stage('Docker Push') {
//                 steps {
//                     sh """
//                       aws ecr get-login-password --region ${AWS_REGION} \
//                       | docker login --username AWS --password-stdin ${ECR_REPO}

//                       docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ECR_REPO}:${IMAGE_TAG}
//                       docker push ${ECR_REPO}:${IMAGE_TAG}
//                     """
//                 }
//             }

//             stage('Cosign Sign') {
//                 steps {
//                     withCredentials([
//                         file(credentialsId: 'cosign-private-key', variable: 'COSIGN_KEY')
//                     ]) {
//                         script {
//                             org.security.CosignSign.sign(
//                                 "${ECR_REPO}:${IMAGE_TAG}",
//                                 env.COSIGN_KEY
//                             )
//                         }
//                     }
//                 }
//             }
//         }
//     }
// }
