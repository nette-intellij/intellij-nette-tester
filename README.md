# Nette Tester integration into PhpStorm

This plugin integrates [Nette Tester](https://tester.nette.org) into PhpStorm IDE.


## Features

- Run Configuration type and editor for Nette Tester.
- Test results are displayed in the dedicated Test Runner tab of the Run tool window.


## Requirements

This plugin is written for PhpStorm 10 and above and is compiled for Java 7.

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
