<?php

use Tester\Runner\OutputHandler;
use Tester\Runner\Runner;


$userSetupScript = \getenv('INTELLIJ_NETTE_TESTER_USER_SETUP');
if ($userSetupScript) {
    require_once $userSetupScript;
}


final class TeamCityOutputHandler implements OutputHandler
{

    /**
     * @var resource
     */
    private $file;


    public function __construct($output = 'php://output')
    {
        $this->file = \fopen($output, 'w');
    }


    public function begin()
    {
         // \fwrite($this->file, $this->message('testCount', array('count' => 0)));
    }


    public function result($testName, $result, $message)
    {
        $flowId = \md5($testName);
        \fwrite($this->file, $this->message('testStarted', array('name' => $testName, 'flowId' => $flowId)));

        if ($result === Runner::SKIPPED) {
            \fwrite($this->file, $this->message('testIgnored', array('name' => $testName, 'flowId' => $flowId, 'message' => 'Test skipped', 'details' => $message)));

        } elseif ($result === Runner::FAILED) {
            $extraArguments = array();
            if (\preg_match("/^diff \"(.*)\" \"(.*)\"$/m", $message, $matches)) { // Windows build
                $expectedFile = \str_replace('""', '"', $matches[1]);
                $actualFile = \str_replace('""', '"', $matches[2]);
                $extraArguments = array('type' => 'comparisonFailure', 'expectedFile' => $expectedFile, 'actualFile' => $actualFile);

            } elseif (\preg_match("/^diff '?(.*)'? '?(.*)'?$/m", $message, $matches)) {
                $expectedFile = \trim($matches[1], "'");
                $actualFile = \trim($matches[2], "'");
                $extraArguments = array('type' => 'comparisonFailure', 'expectedFile' => $expectedFile, 'actualFile' => $actualFile);

            } elseif (\preg_match("/Failed: (.*) should be( equal to)?\s+\.*\s*(.*) in/is", $message, $matches)) {
                $expected = $matches[3];
                $actual = $matches[1];
                $extraArguments = array('type' => 'comparisonFailure', 'expected' => $expected, 'actual' => $actual);
            }

            $args = \array_merge(array(
                'name' => $testName,
                'flowId' => $flowId,
                'message' => 'Test failed',
                'details' => $message,
            ), $extraArguments);

            \fwrite($this->file, $this->message('testFailed', $args));
        }

        \fwrite($this->file, $this->message('testFinished', array('name' => $testName, 'flowId' => $flowId)));
    }


    public function end()
    {
    }


    private function message($messageName, $args)
    {
        $argsPairs = array();
        foreach ($args as $arg => $value) {
            $argsPairs[] = \sprintf("%s='%s'", $arg, $this->escape($value));
        }

        return \sprintf(
            "##teamcity[%s %s]\n\n",
            $messageName,
            \implode(' ', $argsPairs)
        );
    }


    private function escape($value)
    {
        $replace = array(
            "|" => "||",
            "'" => "|'",
            "\n" => "|n",
            "\r" => "|r",
            "]" => "|]",
            "[" => "|[",
        );

        return \strtr($value, $replace);
    }

}


/** @var Runner $runner */
// replace registered output handlers with TC
$runner->outputHandlers = array();
$runner->outputHandlers[] = new TeamCityOutputHandler();
