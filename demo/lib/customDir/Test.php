<?php

  namespace Atl\Ads\Letters;

  use Atl\Letters\UserAddressInfo;

  class RecommendFromNotepadLetter extends \Atl\Letters\Letter {

    use \Funivan\Viewable\Viewable;
    use \Atl\Ads\Partials\AdvertViewTrait;


    /**
     * @var \Atl_Ads_Model[]
     */
    protected $adverts = [];

    /**
     * @var array
     */
    protected $emails = [];

    /**
     * @var string
     */
    protected $subject = '';

    protected $text = '';

    protected $to;

    /**
     * @var \Atl_Locales_Model
     */
    protected $locale;


    /**
     * @param \Atl_Locales_Model $locale
     * @return $this
     */
    public function setLocale(\Atl_Locales_Model $locale) {
      $this->locale = $locale;
      return $this;
    }


    public function __construct($adverts, \Atl_Dealers_Model $dealer) {
      $this->adverts = $adverts;
      $this->dealer = $dealer;
    }


    /**
     * Лист про підтвердження e-mail
     *
     * @return bool
     * @throws \Exception
     */
    public function send() {


      return true;
    }


    /**
     * Тема повідомлення
     *
     * @return string
     */
    public function getSubject() {

    }


    /**
     * Тип листа. Виористовується при відслідковуванні статистики
     * Довжина не більше 32 символи
     *
     * @return string
     */
    public function getType() {
      return 'MyNotepad';
    }


    /**
     * Від кого відправляти повідомлення
     *
     * @return UserAddressInfo
     */
    public function getFrom() {

      if ($this->dealer->code == '8145') {
        $fromName = "M.V. Intertrade";
        $fromEmail = "export@mvintertrade.com";
        $from = UserAddressInfo::createFromCustomUser($this->getLocale()->getDomain(), $fromEmail, $fromName);
      } else {
        $from = UserAddressInfo::createFromDealer($this->dealer);
      }

      $from->hideContacts();
      return $from;
    }


    /**
     * Кому відправляти пошту
     *
     * @return string
     */
    public function getTo() {
      return $this->to;
    }


    /**
     * @param mixed $to
     * @return $this
     */
    public function setTo($to) {
      $this->to = $to;
      return $this;
    }


    /**
     * Тіло листа
     *
     * @return string
     */
    protected function getBodyHtml() {

      $footer = $this->getEmailLayoutObject()->getFooter();
      $header = $this->getEmailLayoutObject()->getHeader();

      //foreach ($this->emails as $toEmail) {
      $email = $this->getTo();

      if (!empty($email)) {
        $emailHash = md5($email . '123');
        $footer->setUnsubscribeLink(
          'http://' . \Atl_Domains::getDomain() . '/email_bl.php?email=' . $email . '&cs=' . $emailHash,
          \Atr::getV('email_unsubscribe_link_text')
        );

      }

      switch ($this->dealer->login) {
        case 'alfaint':
          $header->setCustomLogoImage(IMAGES_DIR . 'alfatruck.com/top_mailing.jpg');
          $this->setAdvertUrlCallback(function (\Atl_Ads_Model $advert) {
            return 'http://alfatruck.com/ru/sales_full.php?p=view&code=' . $advert->code;
          });

          break;

        case 'blitzbet':
          $header->setCustomLogoImage(IMAGES_DIR . 'images/bet-bv.jpg');
          break;

        case 'intertrade':
          $header->setCustomLogoImage(IMAGES_DIR . 'images/mv-intertrade-2.jpg');
          break;

        case 'poppe-trucks':
          $header->setCustomLogoImage(IMAGES_DIR . 'images/poppe-trucks.png');
          break;

        case 'gennyv777':
          $header->setCustomLogoImage(IMAGES_DIR . 'vaisman.de/top_mailing.jpg');

          $footer->setUnsubscribeLink(
            'http://vaisman.de/email_bl.php?email=' . $email . '&cs=' . $emailHash,
            \Atr::getV('email_unsubscribe_link_text')
          );

          $this->setAdvertUrlCallback(function (\Atl_Ads_Model $advert) {
            return 'http://vaisman.de/' . \Atl_Langs::getId() . '/sales_full.php?p=view&code=' . $advert->code;
          });
          break;
        default:
          break;
      }

      $advertPartial = new \Atl\Ads\Partials\AdvertsItemsList([$this->advert]);
      $advertPartial->setAdvertUrlCallback($this->getAdvertUrlCallback());

      return $this->renderView('customTemplate', [
        'text' => $this->text,
        'advertPartial' => $advertPartial,
      ]);
      
    }


    /**
     * На якій мові відправляти лист.
     * Від локалі також залежить залоговок
     *
     * @return \Atl_Locales_Model
     */
    public function getLocale() {
      return $this->locale;
    }

  }
