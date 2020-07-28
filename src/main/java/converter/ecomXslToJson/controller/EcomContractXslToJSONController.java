package converter.ecomXslToJson.controller;

import converter.ecomXslToJson.logic.EcomContractXslToJson;
import converter.ecomXslToJson.logic.IXslToJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("ecomcontract")
public class EcomContractXslToJSONController {
    private IXslToJson ecomContractXslToJson;

    @Autowired
    @Qualifier(value = "ecomContractXslToJson")
    public void setEcomContractXslToJson(EcomContractXslToJson ecomContractXslToJson) {
        this.ecomContractXslToJson = ecomContractXslToJson;
    }

    @GetMapping
    public ResponseEntity status(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(ecomContractXslToJson.status(sid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity run(@RequestParam(value = "sid") String sid, @RequestBody Map<String, Object> body) {
        return new ResponseEntity(ecomContractXslToJson.run(sid, body), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity controls() {
        return new ResponseEntity(ecomContractXslToJson.getControls(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity stop(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(ecomContractXslToJson.stop(sid), HttpStatus.OK);
    }
}
