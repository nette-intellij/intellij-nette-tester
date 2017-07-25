<?php

// Taken from Tester\Helpers
function escapeArg($s) {
    if (preg_match('#^[a-z0-9._=/:-]+\z#i', $s)) {
        return $s;
    }
    return defined('PHP_WINDOWS_VERSION_BUILD')
        ? '"' . str_replace('"', '""', $s) . '"'
        : escapeshellarg($s);
}

$tmpDir = sys_get_temp_dir() . '/intellij-nette-tester';
@mkdir($tmpDir);
array_map('unlink', glob("$tmpDir/*"));

$fileNames = [
    'foo-bar',
    'foo bar baz',
    'foo\' bar',
    'foo" bar',
    'foo \'bar\' baz"',
    'foo "bar" baz',
];

foreach ($fileNames as $name) {
    $expected = $tmpDir . '/' . $name . '.expected';
    $actual = $tmpDir . '/' . $name . '.actual';
    if (!@file_put_contents($expected, 'foo expected') || !@file_put_contents($actual, 'foo actual')) {
	    echo "Can not create: $name\n\n"; // char " is not allowed as filename in Windows
    }
    echo 'diff ' . escapeArg($expected ) . ' ' . escapeArg($actual) . "\n\n";
}
