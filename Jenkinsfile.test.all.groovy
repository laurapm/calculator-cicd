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
                def subject = "ERROR en $jobName #$buildNumber"
                def body = "Revise el pipeline del trabajo $jobName, se ha producido un error en la ejecución #$buildNumber."
                def to = "laura.perez211@comunidadunir.net"
                emailext(body: content, mimeType: 'text/html', replyTo: '$DEFAULT_REPLYTO', subject: subject, to: to, attachLog: true )
            }
        }
    }
}
