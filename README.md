# Nette Tester integration into PhpStorm

This plugin integrates [Nette Tester](https://tester.nette.org) into PhpStorm IDE.


## Features

- Run Configuration type and editor for Nette Tester.
- Test results are displayed in the dedicated Test Runner tab of the Run tool window.


## Installation and requirements

This plugin is written for PhpStorm 10 and above and is compiled for Java 7. You can find it in the Jetbrains plugin repository. Install it from Preferences → Plugins → Browse repositories... and search for `Tester`.

It relies on the TeamCity output format which is currently only implemented in my fork of Nette Tester. Install it via Composer:

```
{
    "require-dev": {
        "nette/tester": "dev-feature/teamcity-output"
    },
    "repositories": [
        {
            "type": "vcs",
            "url": "https://github.com/jiripudil/tester"
        }
    ]
}
```

(The plugin warns you if you try to use it with unsupported versions of Nette Tester.)


## Usage

### Configuration

This plugin provides a new configuration type for Nette Tester:

![Run configuration](doc/run_configuration.png)

- **Test scope** is the directory containing tests you wish to run.
- **Tester executable** specifies path to the Tester runner (`/path/to/your/project/vendor/bin/tester` if you installed Tester via Composer).
- **Tester options** allows you to specify options for Tester script (refer to the [docs](https://tester.nette.org/en/)). You don't need to specify `-p` option, the plugin automatically uses the PHP interpreter associated with the project. Also, path to ini file can be specified separately. And finally, do not force `-o`; this plugin relies on `teamcity` output format which it sets automatically.
- **Path to php.ini** specifies the configuration file to use (`-c` option). You can leave this field blank, in which case tests will run without any configuration loaded.


#### Usage on Windows

Composer seems to do some necessary, but unfortunate transformations on vendor binaries. Therefore you need to point the **Tester executable** option to the actual PHP file in `/path/to/your/project/vendor/nette/tester/src/tester.php`.


### Interpreting results

If you now run this configuration, test results will start to show in the Test Runner window:

![Test results](doc/test_results.png)

To the left, there is a tree view of test suites and tests. You can toggle showing passed and skipped tests. If you click the test, you will see the detailed output in the console window to the right. If you right click the test, you can Jump to Source (or just double click it) to open the test file in the editor, and also run and debug the single test file as a PHP script right away, which can be quite handy to pinpoint what has failed.
