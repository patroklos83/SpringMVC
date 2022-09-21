#!/usr/bin/env groovy
pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw surefire:test'
            }
        }
      stage("deploy"){
       steps{
          sh "sshpass -p 'deployer' scp -o StrictHostKeyChecking=no target/*.war deployer@172.18.0.3:/usr/local/tomcat/webapps/springmvc.war"
          }
        }
     stage('Creating Artifact'){
        steps{
            archiveArtifacts artifacts: "**/*.war", followSymlinks: false
        }
      }
    }
    
      post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
      }
}
