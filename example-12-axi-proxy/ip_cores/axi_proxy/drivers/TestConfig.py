from dataclasses import dataclass
from typing import Dict, Optional


@dataclass
class TestConfig:
    port_name: str
    udmabuf_flags: Optional[int]
    axi_conf: Optional[Dict]
