pipeline {
    agent any

    tools {
        maven 'Maven-3.8.6'
        jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('services/task-service') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('services/task-service') {
                    sh 'mvn test'
                }
            }
        }

        stage('Package') {
            steps {
                dir('services/task-service') {
                    sh 'mvn package -DskipTests'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Сборка task-service успешно завершена!'
        }
        failure {
            echo '❌ Сборка task-service завершилась с ошибкой'
        }
    }
}
