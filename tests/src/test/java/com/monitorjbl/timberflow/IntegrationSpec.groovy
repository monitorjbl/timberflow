package com.monitorjbl.timberflow

import org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

class IntegrationSpec extends Specification {

  def 'Simple test'() {
    given: 'A random set of log-like data'
    def logfile = new File('build/request.log')
    logfile.text = ''
    logfile << generateArtifactoryRequestLogData().join('\n')

    when: 'Timberflow is started'
    println("Starting")
    def output = ""
    def running = true
    def process = "../build/timberflow/bin/timberflow --config ./src/test/resources/pipeline.conf".execute()
    new Thread({
      def is = new BufferedReader(new InputStreamReader(process.inputStream))
      while (running) {
        output += is.readLine() + "\n"
      }
    }).start()
    sleep(30000)
    running = false

    then:
    println(output)

    cleanup:
    process.destroyForcibly()
  }

  def generateArtifactoryRequestLogData() {
    def template = '#TIMESTAMP#|#DURATION#|#TYPE#|#IP#|#USER#|#METHOD#|#PATH#|HTTP/1.1|#RESPONSECODE#|#SIZE#'
    def generateDate = { new Date().format('YYYYMMDDHHmmss') }
    def randomInt = { min, max -> new Random().nextInt(max - min) + min }
    def responseCodes = [200, 201, 202, 302, 304, 400, 401, 403, 404, 500]
    def methods = ['GET', 'PUT', 'POST', 'DELETE', 'HEAD']
    def randomIndex = { arr -> arr[randomInt(0, arr.size() - 1)] }
    def randomWord = { RandomStringUtils.randomAlphabetic(randomInt(4, 12)) }
    def index = 0
    return (0..1000000).collect {
      template.replaceAll('#TIMESTAMP#', generateDate())
          .replaceAll('#DURATION#', index++ as String)
          .replaceAll('#TYPE#', 'REQUEST')
          .replaceAll('#IP#', (0..3).collect { randomInt(1, 256) }.join('.'))
          .replaceAll('#USER#', 'user')
          .replaceAll('#METHOD#', randomIndex(methods) as String)
          .replaceAll('#PATH#', '/' + (0..randomInt(3, 8)).collect { randomWord() }.join('/'))
          .replaceAll('#RESPONSECODE#', randomIndex(responseCodes) as String)
          .replaceAll('#SIZE#', randomInt(32, 100000) as String)
    }
  }
}
