from setuptools import setup, find_packages

setup(
    name="pycompilecheck",
    version="1.0.0",
    description="Python static analysis tool for Django projects",
    author="PyCompileCheck Team",
    packages=find_packages(),
    install_requires=[
        "pydantic>=1.8.0",
        "click>=8.0.0",
        "mypy>=0.910",
        "pylint>=2.9.0",
        "bandit>=1.7.0",
        "safety>=2.0.0",
        "pyyaml>=6.0.0",
        "watchdog>=3.0.0",
    ],
    entry_points={
        "console_scripts": [
            "pycompilecheck=main:main",
        ],
    },
    python_requires=">=3.8",
) 