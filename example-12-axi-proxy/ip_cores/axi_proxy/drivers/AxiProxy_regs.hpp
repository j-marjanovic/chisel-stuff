// auto-generated with AxiLiteSubordinateGenerator from chisel-bfm-tester

#include <cstdint>

struct __attribute__((__packed__)) AxiProxy_regs {
  struct __attribute__((__packed__)) {
    uint32_t ID : 32;
  } ID_REG;

  struct __attribute__((__packed__)) {
    uint32_t PATCH : 8;
    uint32_t MINOR : 8;
    uint32_t MAJOR : 8;
    uint32_t rsvd24 : 1;
    uint32_t rsvd25 : 1;
    uint32_t rsvd26 : 1;
    uint32_t rsvd27 : 1;
    uint32_t rsvd28 : 1;
    uint32_t rsvd29 : 1;
    uint32_t rsvd30 : 1;
    uint32_t rsvd31 : 1;
  } VERSION;

  uint32_t rsvd0x8;

  struct __attribute__((__packed__)) {
    uint32_t FIELD : 32;
  } SCRATCH;

  struct __attribute__((__packed__)) {
    uint32_t DONE_WR : 1;
    uint32_t DONE_RD : 1;
    uint32_t rsvd2 : 1;
    uint32_t rsvd3 : 1;
    uint32_t rsvd4 : 1;
    uint32_t rsvd5 : 1;
    uint32_t rsvd6 : 1;
    uint32_t rsvd7 : 1;
    uint32_t READY_WR : 1;
    uint32_t READY_RD : 1;
    uint32_t rsvd10 : 1;
    uint32_t rsvd11 : 1;
    uint32_t rsvd12 : 1;
    uint32_t rsvd13 : 1;
    uint32_t rsvd14 : 1;
    uint32_t rsvd15 : 1;
    uint32_t rsvd16 : 1;
    uint32_t rsvd17 : 1;
    uint32_t rsvd18 : 1;
    uint32_t rsvd19 : 1;
    uint32_t rsvd20 : 1;
    uint32_t rsvd21 : 1;
    uint32_t rsvd22 : 1;
    uint32_t rsvd23 : 1;
    uint32_t rsvd24 : 1;
    uint32_t rsvd25 : 1;
    uint32_t rsvd26 : 1;
    uint32_t rsvd27 : 1;
    uint32_t rsvd28 : 1;
    uint32_t rsvd29 : 1;
    uint32_t rsvd30 : 1;
    uint32_t rsvd31 : 1;
  } STATUS;

  struct __attribute__((__packed__)) {
    uint32_t START_WR : 1;
    uint32_t START_RD : 1;
    uint32_t rsvd2 : 1;
    uint32_t rsvd3 : 1;
    uint32_t rsvd4 : 1;
    uint32_t rsvd5 : 1;
    uint32_t rsvd6 : 1;
    uint32_t rsvd7 : 1;
    uint32_t DONE_CLEAR : 1;
    uint32_t rsvd9 : 1;
    uint32_t rsvd10 : 1;
    uint32_t rsvd11 : 1;
    uint32_t rsvd12 : 1;
    uint32_t rsvd13 : 1;
    uint32_t rsvd14 : 1;
    uint32_t rsvd15 : 1;
    uint32_t rsvd16 : 1;
    uint32_t rsvd17 : 1;
    uint32_t rsvd18 : 1;
    uint32_t rsvd19 : 1;
    uint32_t rsvd20 : 1;
    uint32_t rsvd21 : 1;
    uint32_t rsvd22 : 1;
    uint32_t rsvd23 : 1;
    uint32_t rsvd24 : 1;
    uint32_t rsvd25 : 1;
    uint32_t rsvd26 : 1;
    uint32_t rsvd27 : 1;
    uint32_t rsvd28 : 1;
    uint32_t rsvd29 : 1;
    uint32_t rsvd30 : 1;
    uint32_t rsvd31 : 1;
  } CONTROL;

  uint32_t rsvd0x18;

  uint32_t rsvd0x1c;

  struct __attribute__((__packed__)) {
    uint32_t CACHE : 4;
    uint32_t rsvd4 : 1;
    uint32_t rsvd5 : 1;
    uint32_t rsvd6 : 1;
    uint32_t rsvd7 : 1;
    uint32_t PROT : 3;
    uint32_t rsvd11 : 1;
    uint32_t rsvd12 : 1;
    uint32_t rsvd13 : 1;
    uint32_t rsvd14 : 1;
    uint32_t rsvd15 : 1;
    uint32_t USER : 2;
    uint32_t rsvd18 : 1;
    uint32_t rsvd19 : 1;
    uint32_t rsvd20 : 1;
    uint32_t rsvd21 : 1;
    uint32_t rsvd22 : 1;
    uint32_t rsvd23 : 1;
    uint32_t rsvd24 : 1;
    uint32_t rsvd25 : 1;
    uint32_t rsvd26 : 1;
    uint32_t rsvd27 : 1;
    uint32_t rsvd28 : 1;
    uint32_t rsvd29 : 1;
    uint32_t rsvd30 : 1;
    uint32_t rsvd31 : 1;
  } CONFIG_AXI;

  struct __attribute__((__packed__)) {
    uint32_t CNTR_WR : 10;
    uint32_t rsvd10 : 1;
    uint32_t rsvd11 : 1;
    uint32_t rsvd12 : 1;
    uint32_t rsvd13 : 1;
    uint32_t rsvd14 : 1;
    uint32_t rsvd15 : 1;
    uint32_t CNTR_RD : 10;
    uint32_t rsvd26 : 1;
    uint32_t rsvd27 : 1;
    uint32_t rsvd28 : 1;
    uint32_t rsvd29 : 1;
    uint32_t rsvd30 : 1;
    uint32_t rsvd31 : 1;
  } STATS;

  uint32_t rsvd0x28;

  uint32_t rsvd0x2c;

  uint32_t rsvd0x30;

  uint32_t rsvd0x34;

  uint32_t rsvd0x38;

  uint32_t rsvd0x3c;

  uint32_t ADDR_LO;  // single reg

  uint32_t ADDR_HI;  // single reg

  uint32_t rsvd0x48;

  uint32_t rsvd0x4c;

  uint32_t rsvd0x50;

  uint32_t rsvd0x54;

  uint32_t rsvd0x58;

  uint32_t rsvd0x5c;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_WR0;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_WR1;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_WR2;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_WR3;

  uint32_t rsvd0x70;

  uint32_t rsvd0x74;

  uint32_t rsvd0x78;

  uint32_t rsvd0x7c;

  uint32_t rsvd0x80;

  uint32_t rsvd0x84;

  uint32_t rsvd0x88;

  uint32_t rsvd0x8c;

  uint32_t rsvd0x90;

  uint32_t rsvd0x94;

  uint32_t rsvd0x98;

  uint32_t rsvd0x9c;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_RD0;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_RD1;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_RD2;

  struct __attribute__((__packed__)) {
    uint32_t DATA : 32;
  } DATA_RD3;

};
