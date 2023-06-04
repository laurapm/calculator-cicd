pipeline {
    agent {
        label 'docker'
    }
    stages {
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
        }
        failure {
            script {
                def jobName = env.JOB_NAME
                def buildNumber = env.BUILD_NUMBER
                mail to: 'laura.perez211@comunidadunir.net',
                    subject: "Failure in $jobName #$buildNumber",
                    body: "El trabajo $jobName #$buildNumber ha fallado. Por favor, revisa el pipeline."
            }
        }
        success {
            junit '**/TEST-*.xml'
            step([$class: 'JUnitResultArchiver', testResults: '**/TEST-*.xml'])
        }
    }
}
