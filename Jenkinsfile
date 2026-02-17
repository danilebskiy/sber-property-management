pipeline {
    agent any

    tools {
        maven 'Maven-3.9.9'
        jdk 'JDK-21'
    }

    stages {
        stage('List Tools') {
            steps {
                bat 'dir C:\\tools\\ || echo No tools found'
            }
        }

        stage('Prepare JDK') {
            steps {
                script {
                    env.JAVA_HOME = tool name: 'JDK-21', type: 'jdk'
                    env.PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
                }
                bat 'java -version'
                bat 'javac -version'
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('services/task-service') {
                    script {
                        def jdkHome = tool name: 'JDK-21', type: 'jdk'
                        bat """
                            set JAVA_HOME=${jdkHome}
                            set PATH=${jdkHome}\\bin;%PATH%
                            echo JAVA_HOME=%JAVA_HOME%
                            java -version
                            mvn clean compile
                        """
                    }
                }
            }
        }

        stage('Test') {
            steps {
                dir('services/task-service') {
                    script {
                        def jdkHome = tool name: 'JDK-21', type: 'jdk'
                        bat """
                            set JAVA_HOME=${jdkHome}
                            set PATH=${jdkHome}\\bin;%PATH%
                            mvn test
                        """
                    }
                }
            }
        }

        stage('Package') {
            steps {
                dir('services/task-service') {
                    script {
                        def jdkHome = tool name: 'JDK-21', type: 'jdk'
                        bat """
                            set JAVA_HOME=${jdkHome}
                            set PATH=${jdkHome}\\bin;%PATH%
                            mvn package -DskipTests
                        """
                    }
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