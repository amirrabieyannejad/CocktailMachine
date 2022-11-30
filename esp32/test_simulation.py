#!/usr/bin/env python3

import pytest
import simulation as sim

class TestServer:
  s = sim.Server()

  def test_init(self):
    assert self.s.ready(), "should be ready on startup"

  def test_ready_cmd(self):
    assert self.s.ready(), "should be ready on startup"

    u = self.s.init_user("test-user")
    self.s.add_command(sim.Command("status", u))
    assert not self.s.ready(), "shouldn't be ready before processing commands"

    self.s.process_queue()
    assert self.s.ready(), "should be ready again after processing commands"
