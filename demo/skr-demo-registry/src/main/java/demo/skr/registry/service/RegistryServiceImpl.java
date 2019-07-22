package demo.skr.registry.service;

import demo.skr.registry.model.AppSvr;
import demo.skr.registry.model.EndPoint;
import demo.skr.registry.model.Permission;
import demo.skr.registry.repository.AppSvrRepository;
import demo.skr.registry.repository.EndPointRepository;
import demo.skr.registry.repository.PermissionRepository;
import org.skr.common.util.BeanUtil;
import org.skr.common.util.JsonUtil;
import org.skr.common.util.tuple.Tuple3;
import org.skr.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistryServiceImpl implements
        RegistryService<AppSvr, Permission, EndPoint> {

    @Autowired
    private AppSvrRepository appSvrRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private EndPointRepository endPointRepository;

    public Tuple3<Long, Long, Long> generatePermissionBits() {
        Permission permission = permissionRepository.findMaxBitPermission();
        if (permission == null) return new Tuple3<>(1L, 1L, 1L);

        long bit1 = permission.bit1 << 1;
        long bit2 = permission.bit2;
        long bit3 = permission.bit3;
        if (bit1 > 0) return new Tuple3<>(bit1, bit2, bit3);

        // carry bit1 to bit2
        bit1 = 1;
        bit2 = bit2 << 1;
        if (bit2 > 0) return new Tuple3<>(bit1, bit2, bit3);

        // carry bit2 to bit3
        bit2 = 1;
        bit3 = bit3 << 1;
        return new Tuple3<>(bit1, bit2, bit3);
    }

    @Override
    public AppSvr getAppSvr(String code) {
        return appSvrRepository.findById(code).orElse(null);
    }

    @Override
    public AppSvr saveAppSvr(AppSvr saving, AppSvr existed) {
        if (existed == null) {
            return appSvrRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed);
            return appSvrRepository.save(existed);
        }
    }

    @Override
    public Permission getPermission(String code) {
        return permissionRepository.findById(code).orElse(null);
    }

    @Override
    public Permission savePermission(Permission saving, Permission existed) {
        if (existed == null) {
            return permissionRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed, "code", "appSvr");
            return permissionRepository.save(existed);
        }
    }

    @Override
    public EndPoint getEndPoint(String url) {
        return endPointRepository.findById(url).orElse(null);
    }

    @Override
    public EndPoint saveEndPoint(EndPoint saving, EndPoint existed) {
        if (existed == null) {
            return endPointRepository.save(saving);
        } else {
            BeanUtil.copyFields(saving, existed, "url", "permission");
            return endPointRepository.save(existed);
        }
    }
}
