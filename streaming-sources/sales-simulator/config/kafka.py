import configparser
from extended_configparser.parser import ExtendedConfigParser
from extended_configparser.interpolator import EnvInterpolation


def get_configs():
    config = ExtendedConfigParser(interpolation=EnvInterpolation())
    config.read("configuration/configuration.ini")

    bootstrap_servers = config["KAFKA"]["bootstrap_servers"]
    auth_method = config["KAFKA"]["auth_method"]
    sasl_username = config["KAFKA"]["sasl_username"]
    sasl_password = config["KAFKA"]["sasl_password"]

    configs = {"bootstrap.servers": bootstrap_servers}

    if auth_method == "sasl_scram":
        configs["security.protocol"] = "SASL_SSL"
        configs["sasl.mechanism"] = "SCRAM-SHA-512"
        configs["sasl.username"] = sasl_username
        configs["sasl.password"] = sasl_password

    print("configs: {0}".format(str(configs)))

    return configs
