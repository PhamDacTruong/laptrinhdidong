package com.admin.appbanhang.model.EventBus;

import com.admin.appbanhang.model.SanPhamMoi;

public class XoaSua {
    SanPhamMoi sanPhamMoi;

    public XoaSua(SanPhamMoi sanPhamMoi) {
        this.sanPhamMoi = sanPhamMoi;
    }

    public SanPhamMoi getSanPhamMoi() {
        return sanPhamMoi;
    }

    public void setSanPhamMoi(SanPhamMoi sanPhamMoi) {
        this.sanPhamMoi = sanPhamMoi;
    }
}
