pipeline {
    agent any
    
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
    
    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
                
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.BUILD_TIMESTAMP = sh(
                        script: 'date +"%Y-%m-%d_%H-%M-%S"',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        
        stage('Build & Test') {
            steps {
                echo 'Building and testing the project (plattformneutral)...'
                sh 'mvn -B -q -DskipTests=false clean verify'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')], 
                                  sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                }
            }
        }
        
        stage('Code Quality: Checkstyle') {
            steps {
                echo 'Running Checkstyle analysis...'
                sh 'mvn -q checkstyle:check'
            }
            post {
                always {
                    recordIssues enabledForFailure: true, 
                                tool: checkStyle(pattern: 'target/checkstyle-result.xml'),
                                qualityGates: [[threshold: 20, type: 'TOTAL', unstable: true]]
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Creating deployable JAR...'
                sh 'mvn -B -q package -DskipTests=true'
                
                archiveArtifacts artifacts: 'target/*.jar', 
                                fingerprint: true,
                                allowEmptyArchive: false
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline fertig.'
            
            cleanWs(
                cleanWhenAborted: true,
                cleanWhenFailure: true,
                cleanWhenNotBuilt: true,
                cleanWhenSuccess: true,
                cleanWhenUnstable: true,
                deleteDirs: true
            )
        }
        
        success {
            echo '✅ Build läuft!'
        }
        
        failure {
            echo '❌ Build Fehler!'
            
            // Bei Fehlern können hier Benachrichtigungen gesendet werden
            // z.B. E-Mail, Slack, Teams, etc.
        }
        
        unstable {
            echo '⚠️ Build instabil! QualityGates oder Tests könnten Probleme aufweisen.'
        }
    }
}
