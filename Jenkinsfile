pipeline {
    agent any

    tools {
       maven 'Maven-3.9.9'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
    stage('Diagnostics') {
        steps {
            sh 'java -version'
            sh 'javac -version'
            sh 'echo $JAVA_HOME'
            sh 'ls -la $JAVA_HOME/bin/javac'
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
