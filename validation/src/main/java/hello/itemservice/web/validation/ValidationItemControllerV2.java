package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }
    // 이 컨트롤러에서만 검증 가능

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 검증 오류 결과를 보관
//        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) { // hasText() : 여기 itemName에 글자가 있냐 없냐?!
//            errors.put("itemName", "상품 이름은 필수 입니다.");
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000원 ~ 1,000,000원 까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000원 ~ 1,000,000원 까지 허용합니다."));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
//            errors.put("quantity", "수량은 최대 9,999개 까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            // 검증 오류 결과를 보관, 모델에다가 errors attributes 정보를 넣어준다.
//            model.addAttribute("errors", errors); modelAttribute에 안담아도 된다 자동으로 view에 같이 넘어간다! 그래서 이 과정 생략 가능
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 검증 오류 결과를 보관
//        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) { // hasText() : 여기 itemName에 글자가 있냐 없냐?!
//            errors.put("itemName", "상품 이름은 필수 입니다.");
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            errors.put("price", "가격은 1,000원 ~ 1,000,000원 까지 허용합니다.");
//            bindingResult.addError(new FieldError("item", "price", "가격은 1,000원 ~ 1,000,000원 까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null,  "가격은 1,000원 ~ 1,000,000원 까지 허용합니다."));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
//            errors.put("quantity", "수량은 최대 9,999개 까지 허용합니다.");
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개 까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null,  "수량은 최대 9,999개 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
                bindingResult.addError(new ObjectError("item",null, null , "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            // 검증 오류 결과를 보관, 모델에다가 errors attributes 정보를 넣어준다.
//            model.addAttribute("errors", errors); modelAttribute에 안담아도 된다 자동으로 view에 같이 넘어간다! 그래서 이 과정 생략 가능
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) { // hasText() : 여기 itemName에 글자가 있냐 없냐?!
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice} , null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

//        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required"); 아래와 동일

        // 검증 로직
        if (!StringUtils.hasText(item.getItemName())) { // hasText() : 여기 itemName에 글자가 있냐 없냐?!
            bindingResult.rejectValue("itemName", "required");
//            new String[]{"required.item.itemName", "required"};
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
//                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice} , null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);

            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm"; // 다시 입력폼으로 보낸다. 왜? 잘못된점을 고지하고 기존에 입력된값은 유지
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 기존에 입력했던값이 왜 계속 남아있는것이야??
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

