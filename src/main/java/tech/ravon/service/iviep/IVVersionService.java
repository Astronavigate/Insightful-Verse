package tech.ravon.service.iviep;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.ravon.model.iviep.IVVersion;

import java.util.List;

public interface IVVersionService extends IService<IVVersion> {

    IVVersion getFileById(String fileId);

    IVVersion getLatestVersion();

    List<IVVersion> getAllVersion();

}
