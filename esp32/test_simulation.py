#!/usr/bin/env python3

import pytest
import simulation as sim

class TestServer:
  def test_init(self):
    s = sim.Server()
    assert s.ready(), "should be ready on startup"

  def test_ready_cmd(self):
    s = sim.Server()
    assert s.ready(), "should be ready on startup"

    u = s.init_user("test-user")
    s.add_command(sim.CmdTest(u))
    assert not s.ready(), "shouldn't be ready before processing commands"

    s.process_queue()
    assert s.ready(), "should be ready again after processing commands"

  def test_drink(self):
    s = sim.Server()
    u = s.init_user("test-user")
    s.add_pump(sim.Pump("water", 1000))

    assert s.liquids() == {"water": 1000}

    s.process(sim.CmdAddLiquid(u, "water", 40))
    assert s.weight == 40

    s.process(sim.CmdAddLiquid(u, "water", 100))
    assert s.weight == 140

    assert s.liquids() == {"water": 860}

  def test_drink2(self):
    s = sim.Server()
    u = s.init_user("test-user")
    s.add_pump(sim.Pump("beer", 1000))
    s.add_pump(sim.Pump("lemonade", 1000))

    assert s.liquids() == {"beer": 1000, "lemonade": 1000}

    s.process(sim.CmdAddLiquid(u, "beer", 300))
    assert s.weight == 300

    s.process(sim.CmdAddLiquid(u, "lemonade", 200))
    assert s.weight == 500

    assert s.liquids() == {"beer": 700, "lemonade": 800}
